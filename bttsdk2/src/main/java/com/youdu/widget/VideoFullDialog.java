package com.youdu.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.example.bttsdk.R;
import com.youdu.constant.SDKConstant;
import com.youdu.module.AdValue;
import com.youdu.report.ReportManager;


/**
 * Created by mycomputer on 2017/5/1.
 */

public class VideoFullDialog extends Dialog implements CustomVideoView.ADVideoPlayerListener {

    private static final String TAG = VideoFullDialog.class.getSimpleName();

    /**
     * UI
     */
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;
    private ImageView mBackButton;

    /**
     * Data
     */
    private AdValue mXAdInstance;
    private FullToSmallListener mListener;
    private int mPosition;//从小屏到全屏时视频的播放位置
    private boolean isFirst = true;

    public VideoFullDialog(@NonNull Context context,CustomVideoView videoView,  AdValue instance,int position) {
        super(context, R.style.dialog_full_screen);//通过style的设置，保证Dialog全屏
        mXAdInstance = instance;
        mVideoView = videoView;
        mPosition = position;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.xadsdk_dialog_video_layout);
        initVideoView();
    }


    private void initVideoView() {
        mParentView=(RelativeLayout)findViewById(R.id.content_layout);
        mBackButton=(ImageView)findViewById(R.id.xadsdk_player_close_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBackBtn();
            }
        });
        mVideoView.setListener(this);//设置监听事件为当前对话框
        mVideoView.mute(false);
        mParentView.addView(mVideoView);
    }

    /**
     * 全屏返回关闭按钮点击事件
     */
    private void clickBackBtn() {
        dismiss();
        if(mListener != null){
            mListener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
        }
    }

    /**
     * 返回键按下的事件监听
     */
    @Override
    public void onBackPressed() {
        clickBackBtn();
        super.onBackPressed();
    }

    /**
     * 焦点状态改变时的回调
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.w(TAG, "onWindowFocusChanged: ");
        if(!hasFocus){
            //未获取到焦点是逻辑
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }else{
            //获取到焦点
            //如果是TRUE 表明dialog是首次创建且首次获得焦点，
            if(isFirst){
                mVideoView.seekAndResume(mPosition);
            }else{
                mVideoView.resume();//恢复视频播放
            }
        }
        isFirst = false;
    }

    /**
     * dialog销毁的时候调用
     */
    @Override
    public void dismiss() {
        Log.w(TAG, "dismiss: ");
        mParentView.removeView(mVideoView);
        super.dismiss();
    }

    /*******************************************
     *  实现了ADVideoPlayerListener接口中的方法 *
     *******************************************/
    @Override
    public void onBufferUpdate(int time) {
        try {
            if (mXAdInstance != null) {
                ReportManager.suReport(mXAdInstance.middleMonitor, time / SDKConstant.MILLION_UNIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickFullScreenBtn() {
        onClickVideo();
    }

    @Override
    public void onClickVideo() {
    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    @Override
    public void onAdVideoLoadComplete() {
        try {
            int position = mVideoView.getDuration() / SDKConstant.MILLION_UNIT;
            ReportManager.sueReport(mXAdInstance.endMonitor, true, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dismiss();
        if (mListener != null) {
            mListener.playComplete();
        }
    }


    /**
     * 注入事件监听类
     * @param listener
     */
    public void setListener(FullToSmallListener listener){
        mListener=listener;
    }

    /**
     * 与业务逻辑层（VideoAdSlot）进行通信
     */
    public interface FullToSmallListener{
        void getCurrentPlayPosition(int position);//全屏播放中点击关闭按钮或者按back键返回时回调
        void playComplete();//全屏播放结束时回调
    }
}
