package com.youdu.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.Surface;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.bttsdk.R;
import com.youdu.constant.SDKConstant;
import com.youdu.util.Utils;

/**
 * Created by mycomputer on 2017/4/17.
 */

public class CustomVideoView extends RelativeLayout implements View.OnClickListener,
        MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,
        OnCompletionListener,MediaPlayer.OnBufferingUpdateListener ,
        TextureView.SurfaceTextureListener{

    /**
     * constant
     */
    private static final String TAG = "MraidVideoView";
    private static final int TIME_MSG=0x01;
    private static final int TIME_INVAL=1000;
    /**
     * 播放器生命周期状态
     */
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE =0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING =2;

    private static final int LOAD_TOTAL_COUNT = 3;

    /**
     *UI
     */
    private ViewGroup mParentContainer;
    private RelativeLayout mPlayerView;
    private TextureView mVideoView;
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private ImageView mFrameView;
    private AudioManager audioManager;//音量控制
    private Surface videoSurface;//真正显示帧数据的类

    /**
     * Data
     */
    private String mUrl;//要加载的视频地址
    private boolean isMute;//是否静音
    private int mScreenWidth,mDestationHeight;//屏幕的宽高


    /**
     * Status状态标志
     */
    private boolean mIsRealPause;
    private boolean mIsComplete;
    private int mCurrentCount;



    private int playerState =STATE_IDLE;//当前处于哪个状态，默认处于空闲状态

    private MediaPlayer mediaPlayer;
    private ADVideoPlayerListener listener;//事件监听回调
    private ScreenEventReceiver mScreenReceiver;//监视屏幕是否锁屏
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case TIME_MSG:
                    if(isPlaying()){
                        listener.onBufferUpdate(getCurrentPosition());
                        sendEmptyMessageDelayed(TIME_MSG,TIME_INVAL);
                    }
                    break;
            }
        }
    };

    /**
     * true is no voice
     *
     * @param mute
     */
    public void mute(boolean mute) {
        Log.d(TAG, "mute");
        isMute = mute;
        if (mediaPlayer != null && this.audioManager != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    public CustomVideoView(Context context,ViewGroup ParentContainer) {
        super(context);
        mParentContainer = ParentContainer;
        audioManager = (AudioManager)getContext()
                .getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();
        registerBroadcastReceiver();
    }

    private void initData(){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager)getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mDestationHeight = (int)(mScreenWidth* SDKConstant.VIDEO_HEIGHT_PERCENT);
    }

    private void initView(){
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        mPlayerView = (RelativeLayout)inflater.inflate(R.layout.xadsdk_video_player,this);
        mVideoView = (TextureView)mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setSurfaceTextureListener(this);

        LayoutParams params = new LayoutParams(mScreenWidth,mDestationHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);

        mMiniPlayBtn = (Button)mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = (ImageView)mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mLoadingBar =(ImageView)mPlayerView.findViewById(R.id.loading_bar);
        mFrameView = (ImageView)mPlayerView.findViewById(R.id.framing_view);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);



    }

    /**
     * 在view的显示发生改变时，回调此方法
     * @param changedView
     * @param visibility
     */
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        Log.e(TAG, "onVisibilityChanged:"+visibility);
        super.onVisibilityChanged(changedView,visibility);
        if(visibility == VISIBLE && playerState == STATE_PAUSING){
            //决定是否播放
            if(mIsRealPause|| isComplete()){
                //表明播放器进入了真正的暂停状态
                pause();
            }else{
                decideCanPlay();
            }
        }else{
            pause();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == this.mMiniPlayBtn) {
            Log.w(TAG, "onClick: 我被点了！！！");
            if (this.playerState == STATE_PAUSING) {
                Log.w(TAG, "onClick: 是暂停状态！！！");
                if (Utils.getVisiblePercent(mParentContainer)
                        > SDKConstant.VIDEO_SCREEN_PERCENT) {
                    resume();
                    this.listener.onClickPlay();
                }
            } else {
                load();
            }
        } else if (v == this.mFullBtn) {
            this.listener.onClickFullScreenBtn();
        } else if (v == mVideoView) {
            this.listener.onClickVideo();
        }
        // TODO: 2017/5/3 add pause and resume video at there
    }

    /**
     * 在播放器播放完成后会回调该方法
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(listener != null){
            listener.onAdVideoLoadComplete();
        }
        setIsComplete(true);
        setIsRealPause(true);
        playBack();
    }

    /**
     * 播放器产生异常的时候回调
     * @param mp
     * @param what
     * @param extra
     * @return 返回TRUE表明自己已经处理了异常，Android系统不会帮你处理异常，如果返回FALSE则表明自己没有处理异常，Android系统会帮你以默认方式处理
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        this.playerState = STATE_ERROR;
        if(mCurrentCount>=LOAD_TOTAL_COUNT){
            if(listener != null){
                listener.onAdVideoLoadFailed();
            }
            showPauseView(false);
        }
        stop();
        return true;
    }

    /**
     * 播放器处于就绪状态
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        showPlayView();
        mediaPlayer = mp;
        Log.w(TAG, "——————————————————————————");
        Log.w(TAG, "onPrepared: 视频加载完成！"+mp);
        if(mediaPlayer != null){
            Log.w(TAG, "onPrepared: 播放器不为空！");
            mediaPlayer.setOnBufferingUpdateListener(this);
            mCurrentCount = 0;
            this.playerState = STATE_PAUSING;
            if(listener != null){
                listener.onAdVideoLoadSuccess();
            }
            decideCanPlay();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * 表明我们的TextureView处于就绪状态
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable: ");
        videoSurface = new Surface(surface);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    public void setDataSource(String url){
        this.mUrl=url;
    }

    /**
     * 加载视频url
     */
    public void load(){
        if(this.playerState != STATE_IDLE){
            return;
        }
        try {
            showLoadingView();//显示加载时的布局
            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer();//完成创建mediaPlayer工作
            mediaPlayer.setDataSource(mUrl);
            mediaPlayer.prepareAsync();//异步加载视频
        }catch (Exception e){
            stop();
        }
    }

    /**
     * 暂停视频
     */
    public void pause(){
        if(this.playerState != STATE_PLAYING){
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if(isPlaying()){
            //真正的完成暂停
            mediaPlayer.pause();
        }
        showPauseView(false);
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 恢复视频播放
     */
    public void resume(){
        if(this.playerState != STATE_PAUSING){
            Log.w(TAG, "播放状态为暂停！！！");
            return;
        }
        if(mediaPlayer == null){
            Log.w(TAG, "mediaPlayer为空");
        }
        if(!isPlaying()){
            Log.w(TAG, "——————————————————————————");
            Log.w(TAG, "准备播放");
            entryResumeState();//设置为播放中的状态值
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.start();
            showPauseView(true);
            mHandler.sendEmptyMessage(TIME_MSG);
        }else{
            showPauseView(false);
        }
    }

    /**
     * 播放完成后回到初始状态
     */
    public void playBack(){
        setCurrentPlayState(STATE_PAUSING);
        mHandler.removeCallbacksAndMessages(null);
        if(mediaPlayer != null){
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
        showPauseView(false);
    }

    /**
     * 停止状态
     */
    public void stop(){
        Log.d(TAG, "do stop: ");
        if(this.mediaPlayer != null){
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        setCurrentPlayState(STATE_IDLE);

        //去从新load
        if(mCurrentCount<LOAD_TOTAL_COUNT){
            mCurrentCount += 1;
            load();
        }else{
            //停止重试
            showPauseView(false);
        }
    }

    /**
     * 销毁我们当前自定义的View
     */
    public void destroy(){

    }

    /**
     * 跳转并播放
     * @param position
     */
    public void seekAndResume(int position){
        if (mediaPlayer != null) {
            showPauseView(true);
            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.w(TAG, "do seek and resume");
                    mediaPlayer.start();
                    mHandler.sendEmptyMessage(TIME_MSG);
                }
            });
        }
    }

    /**
     * 跳转并暂停
     * @param position
     */
    public void seekAndPause(int position){
        if(this.playerState != STATE_PLAYING){
            return;
        }
        showPauseView(false);
        setCurrentPlayState(STATE_PAUSING);
        if(isPlaying()){
            mediaPlayer.seekTo(position);
            //由于seekTo方法的时间不确定，所以需要设置SeekTo的事件监听
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener(){

                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    Log.w(TAG, "do seek and pause");
                    /**
                     * 跳转事件结束后，暂停视频
                     */
                    mediaPlayer.pause();
                    mHandler.removeCallbacksAndMessages(null);
                }
            });
        }
    }

    public void setListener(ADVideoPlayerListener listener){
        this.listener = listener;
    }


    private synchronized void checkMediaPlayer(){
        if(mediaPlayer == null){
            mediaPlayer = createMediaPlayer();
        }
    }
    private MediaPlayer createMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if(videoSurface != null&&videoSurface.isValid()){
            mediaPlayer.setSurface(videoSurface);
        }else{
            stop();
        }
        return mediaPlayer;
    }



    private void registerBroadcastReceiver() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, filter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (mScreenReceiver != null) {
            getContext().unregisterReceiver(mScreenReceiver);
        }
    }

    public void isShowFullBtn(boolean isShow){
        //// TODO: 2017/4/17
    }

    public boolean isPauseBtnClicked(){
        return mIsRealPause;
    }

    public boolean isComplete(){
        return mIsComplete;
    }
    public boolean isRealPause(){return mIsRealPause;}

    public void showPauseView(boolean show){
        Log.w(TAG, "————————————————————————————" );
        Log.w(TAG, "showPauseView: 显示暂停界面" );
        mFullBtn.setVisibility(show ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        if (!show) {
            mFrameView.setVisibility(View.VISIBLE);
            loadFrameImage();
        } else {
            mFrameView.setVisibility(View.GONE);
        }
    }

    public void showLoadingView(){
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable)mLoadingBar.getBackground();
        anim.start();
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
        loadFrameImage();

    }

    public void showPlayView(){
        //// TODO: 2017/4/17
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
    }

    private void loadFrameImage() {

    }

    /**
     * 进入播放状态的状态更新
     */
    private void entryResumeState() {
        setCurrentPlayState(STATE_PLAYING);
        setIsRealPause(false);
        setIsComplete(false);
    }

    private void setCurrentPlayState(int state) {
        playerState = state;
    }
    
    public int getDuration(){
        if(mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getCurrentPosition(){
        if(this.mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    private void decideCanPlay() {

        Log.w(TAG, "——————————————————————————");
        Log.w(TAG, "onPrepared: 执行判断是否播放方法！");
        if (
//                Utils.canAutoPlay(getContext(),SDKConstant.getCurrentSetting())&&
                        Utils.getVisiblePercent(mParentContainer) >= SDKConstant.VIDEO_SCREEN_PERCENT
                )
            //来回切换页面时，只有 >50,且满足自动播放条件才自动播放
        {
            Log.w(TAG, "符合播放条件");
            resume();
        }else{
            pause();
        }
    }

    public void setIsComplete(boolean isComplete) {
        this.mIsComplete = isComplete;
    }

    public void setIsRealPause(boolean isRealPause) {
        this.mIsRealPause = isRealPause;
    }

    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //主动锁屏时 pause, 主动解锁屏幕时，resume
            switch (intent.getAction()) {
                case Intent.ACTION_USER_PRESENT:
                    if (playerState == STATE_PAUSING) {
                        if (mIsRealPause) {
                            //手动点的暂停，回来后还暂停
                            pause();
                            //Todo:1
                        } else {
                           decideCanPlay();
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    if (playerState == STATE_PLAYING) {
                       pause();
                    }
                    break;
            }
        }
    }
    /**
     * 供slot层来实现具体点击逻辑,具体逻辑还会变，
     * 如果对UI的点击没有具体监测的话可以不回调
     */
    public interface ADVideoPlayerListener {
        void onBufferUpdate(int time);//视频播放到了第几秒
        void onClickFullScreenBtn();//跳转全屏播放事件的监听
        void onClickVideo();
        void onClickBackBtn();
        void onClickPlay();
        void onAdVideoLoadSuccess();
        void onAdVideoLoadFailed();
        void onAdVideoLoadComplete();
    }

    public interface ADFrameImageLoadListener {

        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }

    public interface ImageLoaderListener {
        /**
         * 如果图片下载不成功，传null
         *
         * @param loadedImage
         */
        void onLoadingComplete(Bitmap loadedImage);
    }

}
