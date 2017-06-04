package com.youdu.view.fragment.home;


import com.example.mycomputer.imoocbusiness1.R;
import com.youdu.activity.SettingActivity;
import com.youdu.module.update.UpdateModel;
import com.youdu.network.http.RequestCenter;
import com.youdu.okhttp.listener.DisposeDataListener;
import com.youdu.util.Util;
import com.youdu.view.CommonDialog;
import com.youdu.view.fragment.BaseFragment;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;


import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mycomputer on 2017/3/29.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener {
    /**
     * UI
     */
    private View mContentView;
    private RelativeLayout mLoginLayout;
    private CircleImageView mPhotoView;
    private TextView mLoginInfoView;
    private TextView mLoginView;
    private RelativeLayout mLoginedLayout;
    private TextView mUserNameView;
    private TextView mTickView;
    private TextView mVideoPlayerView;
    private TextView mShareView;
    private TextView mQrCodeView;
    private TextView mUpdateView;

    //自定义了一个广播接收器
//    private LoginBroadcastReceiver receiver = new LoginBroadcastReceiver();

    public MineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_mine_layout, null, false);
        initView();
        return mContentView;
    }

    private void initView() {
        mLoginLayout = (RelativeLayout) mContentView.findViewById(R.id.login_layout);
        mLoginLayout.setOnClickListener(this);
        mLoginedLayout = (RelativeLayout) mContentView.findViewById(R.id.logined_layout);
        mLoginedLayout.setOnClickListener(this);

        mPhotoView = (CircleImageView) mContentView.findViewById(R.id.photo_view);
        mPhotoView.setOnClickListener(this);
        mLoginView = (TextView) mContentView.findViewById(R.id.login_view);
        mLoginView.setOnClickListener(this);
        mVideoPlayerView = (TextView) mContentView.findViewById(R.id.video_setting_view);
        mVideoPlayerView.setOnClickListener(this);
        mShareView = (TextView) mContentView.findViewById(R.id.share_imooc_view);
        mShareView.setOnClickListener(this);
        mQrCodeView = (TextView) mContentView.findViewById(R.id.my_qrcode_view);
        mQrCodeView.setOnClickListener(this);
        mLoginInfoView = (TextView) mContentView.findViewById(R.id.login_info_view);
        mUserNameView = (TextView) mContentView.findViewById(R.id.username_view);
        mTickView = (TextView) mContentView.findViewById(R.id.tick_view);

        mUpdateView = (TextView) mContentView.findViewById(R.id.update_view);
        mUpdateView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_setting_view:
                mContext.startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.update_view:
                checkVersion();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 发送版本更新请求
     */
    private void checkVersion(){
        RequestCenter.checkVersion(new DisposeDataListener() {
            //请求成功回调
            @Override
            public void onSuccess(Object responseObj) {
                final UpdateModel updateModel = (UpdateModel)responseObj;
                if(Util.getVersionCode(mContext)<updateModel.data.currentVersion){
                    //说明有新版本
                    CommonDialog dialog = new CommonDialog(mContext,
                            getString(R.string.update_new_version),
                            getString(R.string.update_title),
                            getString(R.string.update_install),
                            getString(R.string.cancel),
                            new CommonDialog.DialogClickListener() {
                        @Override
                        public void onDialogClick() {
                            //安装事件回调处理,就是启动我们的更新服务。
                            Intent intent = new Intent(mContext,UpdateService.class);
                            mContext.startService(intent);
                        }
                    });
                    dialog.show();
                }else{
                    //当前为最新版本
                }
            }
            //请求失败回调
            @Override
            public void onFailure(Object reasonObj) {

            }
        });
    }


}
