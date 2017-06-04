package com.youdu.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.mycomputer.imoocbusiness1.R;
import com.youdu.constant.SDKConstant;
import com.youdu.db.SharedPreferenceManager;

import android.view.View;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * UI
     */
    private RelativeLayout mWifiLayout;
    private RelativeLayout mAlwaysLayout;
    private RelativeLayout mNeverLayout;
    private CheckBox mWifiBox,mAlwaysBox,mNeverBox;
    private ImageView mBackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_layout);
        initView();
    }

    //初始化所用到的控件
    private void initView(){
        mBackView = (ImageView) findViewById(R.id.back_view);
        mWifiLayout = (RelativeLayout) findViewById(R.id.wifi_layout);
        mWifiBox = (CheckBox) findViewById(R.id.wifi_check_box);
        mAlwaysLayout = (RelativeLayout) findViewById(R.id.alway_layout);
        mAlwaysBox = (CheckBox) findViewById(R.id.alway_check_box);
        mNeverLayout = (RelativeLayout) findViewById(R.id.close_layout);
        mNeverBox = (CheckBox) findViewById(R.id.close_check_box);
        mBackView.setOnClickListener(this);
        mWifiLayout.setOnClickListener(this);
        mAlwaysLayout.setOnClickListener(this);
        mNeverLayout.setOnClickListener(this);

        int currentSetting = SharedPreferenceManager.getInstance().getInt(
                SharedPreferenceManager.VIDEO_SETTING,1
        );
        switch (currentSetting){
            case 0:
                mAlwaysBox.setBackgroundResource(R.drawable.setting_selected);
                mWifiBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(0);
                break;
            case 1:
                mWifiBox.setBackgroundResource(R.drawable.setting_selected);
                mAlwaysBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(0);
                break;
            case 2:
                mNeverBox.setBackgroundResource(R.drawable.setting_selected);
                mWifiBox.setBackgroundResource(0);
                mAlwaysBox.setBackgroundResource(0);
                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.alway_layout:
                Toast.makeText(this, "always", Toast.LENGTH_SHORT).show();
                SharedPreferenceManager.getInstance().putInt(SharedPreferenceManager.VIDEO_SETTING,0);
//                SDKConstant.setCurrentSetting(SDKConstant.AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI);
                mAlwaysBox.setBackgroundResource(R.drawable.setting_selected);
                mWifiBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(0);
                break;
            case R.id.close_layout:
                Toast.makeText(this, "never", Toast.LENGTH_SHORT).show();
                SharedPreferenceManager.getInstance().putInt(SharedPreferenceManager.VIDEO_SETTING,2);
//                SDKConstant.setCurrentSetting(SDKConstant.AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI);
                mNeverBox.setBackgroundResource(R.drawable.setting_selected);
                mWifiBox.setBackgroundResource(0);
                mAlwaysBox.setBackgroundResource(0);
                break;
            case R.id.wifi_layout:
                Toast.makeText(this, "wifi", Toast.LENGTH_SHORT).show();
                SharedPreferenceManager.getInstance().putInt(SharedPreferenceManager.VIDEO_SETTING,1);
//                SDKConstant.setCurrentSetting(SDKConstant.AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI);
                mWifiBox.setBackgroundResource(R.drawable.setting_selected);
                mAlwaysBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(0);
                break;
            case R.id.back_view:
                finish();
                break;
        }
    }
}
