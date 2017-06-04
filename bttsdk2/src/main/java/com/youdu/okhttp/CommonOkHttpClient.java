package com.youdu.okhttp;

import com.youdu.okhttp.https.HttpsUtils;
import com.youdu.okhttp.listener.DisposeDataHandle;
import com.youdu.okhttp.response.CommonJsonCallback;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by mycomputer on 2017/3/30.
 * @function 请求的发送，请求参数的配置，HTTPS的支持。
 */

public class CommonOkHttpClient {
    private static final int TIME_OUT = 30;//超时参数
    private static OkHttpClient mOkHttpClient;

    //为我们的client去配置参数
    static {
        //创建client个对象的构建者
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        //为构建者填充超时时间
        okHttpBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(TIME_OUT,TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(TIME_OUT,TimeUnit.SECONDS);

        okHttpBuilder.followRedirects(true);

        //https支持
        okHttpBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        okHttpBuilder.sslSocketFactory(HttpsUtils.initSSLSocketFactory(), HttpsUtils.initTrustManager());

        //生成client对象
        mOkHttpClient = okHttpBuilder.build();
    }

    /**
     * 发送具体的http/https请求
     * @param request
     * @param commCallback
     * @return
     */
    public static Call sendRequest(Request request, CommonJsonCallback commCallback){

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(commCallback);

        return call;
    }

    public static Call get(Request request, DisposeDataHandle handle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }

    public static Call post(Request request, DisposeDataHandle handle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }
}
