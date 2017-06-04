package com.youdu.network.http;

import com.youdu.module.recommand.BaseRecommandModel;
import com.youdu.module.update.UpdateModel;
import com.youdu.okhttp.CommonOkHttpClient;
import com.youdu.okhttp.listener.DisposeDataHandle;
import com.youdu.okhttp.listener.DisposeDataListener;
import com.youdu.okhttp.request.CommonRequest;
import com.youdu.okhttp.request.RequestParams;

/**
 * Created by mycomputer on 2017/4/3.
 * @function 存放应用中所有的请求
 */

public class RequestCenter {
    //根据参数发送所有pose请求
    private static void postRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz){
        CommonOkHttpClient.get(CommonRequest.createGetRequest(url,params),new DisposeDataHandle(listener,clazz));
    }

    /**
     *真正的发送首页请求
     */
    public static void requestRecommandData(DisposeDataListener listener){
        RequestCenter.postRequest(HttpConstants.HOME_RECOMMAND,null,listener,BaseRecommandModel.class);
    }

    /**
     * 应用版本号请求
     *
     * @param listener
     */
    public static void checkVersion(DisposeDataListener listener) {
        RequestCenter.postRequest(HttpConstants.CHECK_UPDATE, null, listener, UpdateModel.class);
    }

}
