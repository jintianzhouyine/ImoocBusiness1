package com.youdu.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.youdu.application.ImoocApplication;

/**
 * Created by mycomputer on 2017/5/27.
 */

public class SharedPreferenceManager {
    //当前类的实例
    private static SharedPreferenceManager mInstance;

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    private static final String SHARE_PREFERENCE_NAME = "monday.pre";//文件名
    public static final String VIDEO_SETTING = "video_setting";//播放设置
    public static SharedPreferenceManager getInstance(){
        if (mInstance == null){
            mInstance = new SharedPreferenceManager();
        }
        return mInstance;
    }

    private SharedPreferenceManager(){
        sp = ImoocApplication.getInstance().getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    //对int类型的写入
    public void putInt(String key,int value){
        editor.putInt(key,value);
        editor.commit();
    }

    //对int的类型的读取
    public int getInt(String key,int defaultValue){
        return sp.getInt(key,defaultValue);
    }
}
