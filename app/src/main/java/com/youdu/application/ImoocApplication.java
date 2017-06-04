package com.youdu.application;

/**
 * Created by mycomputer on 2017/3/29.
 */

import android.app.Application;

/**
 * @function 1.他是整个程序的入口
 * 2.初始化工作
 * 3.提供上下文
 */

public class ImoocApplication extends Application {
    private static ImoocApplication mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
    public static ImoocApplication getInstance(){
        return mApplication;
    }
}
