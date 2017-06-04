package com.youdu.core;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.youdu.constant.SDKConstant;
import com.youdu.module.AdValue;
import com.youdu.report.ReportManager;
import com.youdu.util.Utils;
import com.youdu.widget.CustomVideoView;
import com.youdu.widget.VideoFullDialog;

import static android.content.ContentValues.TAG;

/**
 * Created by mycomputer on 2017/4/20.
 */

public class VideoAdSlot implements CustomVideoView.ADVideoPlayerListener {

    private Context mContext;
    /**
     * UI
     */
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;//要添加到的父容器中

    /**
     * Data
     */
    private AdValue mVideoInfo;
    private AdSDKSlotListener mSlotListener;
    private boolean canPause =false;//是否可自动暂停标志位
    private int lastArea = 0;//防止将要滑入滑出时播放器的状态改变

    public VideoAdSlot(AdValue adInstance,AdSDKSlotListener slotListener){
        mVideoInfo = adInstance;
        mSlotListener = slotListener;
        mParentView = slotListener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView();
    }

    private void initVideoView() {
        mVideoView = new CustomVideoView(mContext,mParentView);
        if(mVideoInfo != null){
            mVideoView.setDataSource(mVideoInfo.resource);
            mVideoView.setListener(this);
        }
//        RelativeLayout paddingView = new RelativeLayout(mContext);
//        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.background_dark));
//        paddingView.setLayoutParams(mVideoView.getLayoutParams());
//        mParentView.addView(paddingView);

        mParentView.addView(mVideoView);
    }

    public void updateVideoInScrollView(){
        int currentArea = Utils.getVisiblePercent(mParentView);
        //还未出现在屏幕上，不做任何处理
        if(currentArea <=0){
            return;
        }
        //刚要滑入和滑出的时候的异常情况的处理
        if(Math.abs(currentArea - lastArea) >=100){
            return;
        }
        //滑动没有超过50%的时候，让播放器暂停
        if(currentArea <= SDKConstant.VIDEO_SCREEN_PERCENT){
            if(canPause){
                pauseVideo();
                canPause = false;//滑动事件的过滤
            }
            lastArea = 0;
            mVideoView.setIsComplete(false);
            mVideoView.setIsRealPause(false);
            return;
        }
        //当视频进入真正的暂停状态时走此case
        if(isRealPause() || isComplete()){
            pauseVideo();
            canPause = false;
            return;
        }
        //超过50%的时候让播放器进入播放状态
        if(Utils.canAutoPlay(mContext,AdParameters.getCurrentSetting())){
            //真正的去播放视频
            resumeVideo();
            canPause=true;
            lastArea=currentArea;
        }else{
            pauseVideo();
            mVideoView.setIsRealPause(true);
        }

    }


    @Override
    public void onBufferUpdate(int time) {
        try {
            ReportManager.suReport(mVideoInfo.middleMonitor, time / SDKConstant.MILLION_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从小屏到全屏的播放功能
     */
    @Override
    public void onClickFullScreenBtn() {
        //将播放器从View树种移出
        mParentView.removeView(mVideoView);
        //创建全屏播放dialog
        VideoFullDialog dialog = new VideoFullDialog(mContext,mVideoView,mVideoInfo,getPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                //在全屏视频播放的时候点击了返回
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                //全屏播放完以后的事件回调
                bigPlayComplete();
            }
        });
        dialog.show();//全屏显示Dialog
    }

    /**
     * 全屏播放结束时的事件回调
     */
    private void bigPlayComplete() {
        if(mVideoView.getParent() == null){
            mParentView.addView(mVideoView);
        }
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndPause(0);
        canPause = false;
    }

    /**
     * 返回小屏模式的时候
     * @param position
     */
    private void backToSmallMode(int position) {
        if(mVideoView.getParent() == null){
            mParentView.addView(mVideoView);
        }
        mVideoView.isShowFullBtn(true);//显示我们的全屏按钮
        mVideoView.mute(true);//小屏静音播放
        mVideoView.setListener(this);//重新设置监听为业务逻辑层
        mVideoView.seekAndResume(position);//使播放器跳到指定的位置并播放
    }

    @Override
    public void onClickVideo() {
        String desationUrl = mVideoInfo.clickUrl;
        //跳转到webView页面
        if(!TextUtils.isEmpty(desationUrl)){
//            Intent intent = new Intent(mContext,AdBrowserActivity.class);
//            intent.putExtra(AdBrowserActivity.KEY_URL,mVideoInfo.clickUrl);
//            mContext.startActivity(intent);
            Toast.makeText(mContext, "我就不出广告！！！", Toast.LENGTH_SHORT).show();
        }
        if(mVideoView.isPlaying()){
            mVideoView.pause();
        }
}

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if(mSlotListener != null){
            mSlotListener.onAdVideoLoadSuccess();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {
        if(mSlotListener != null){
            mSlotListener.onAdVideoLoadFailed();
        }
        canPause = false;
    }

    @Override
    public void onAdVideoLoadComplete() {
        try {
            ReportManager.sueReport(mVideoInfo.endMonitor,false,getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(mSlotListener !=null){
            mSlotListener.onAdVideoLoadComplete();
        }
        mVideoView.setIsRealPause(true);
    }

    /**
     * 获取当前播放到了第几秒
     * @return
     */
    private int getPosition(){
        return mVideoView.getCurrentPosition()/SDKConstant.MILLION_UNIT;
    }

    /**
     * 获取视频总共有多长时间
     * @return
     */
    private int getDuration(){
        return mVideoView.getDuration()/SDKConstant.MILLION_UNIT;
    }

    private boolean isPlaying(){
        if(mVideoView !=null){
            return mVideoView.isPlaying();
        }
        return false;
    }

    private boolean isRealPause(){
        if(mVideoView != null){
            return mVideoView.isRealPause();
        }
        return false;
    }

    private boolean isComplete(){
        if (mVideoView != null) {
            return mVideoView.isComplete();
        }
        return false;
    }


    private void pauseVideo(){
        if(mVideoView != null){
            mVideoView.seekAndPause(getPosition());
        }
    }

    private void resumeVideo(){
        if(mVideoView != null){
            mVideoView.resume();
        }
    }
    //传递消息到appcontext层
    public interface AdSDKSlotListener {

        public ViewGroup getAdParent();

        public void onAdVideoLoadSuccess();

        public void onAdVideoLoadFailed();

        public void onAdVideoLoadComplete();

        public void onClickVideo(String url);
    }

}
