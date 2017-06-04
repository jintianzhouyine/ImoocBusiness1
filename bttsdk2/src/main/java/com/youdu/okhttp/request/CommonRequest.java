package com.youdu.okhttp.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by mycomputer on 2017/3/30.
 * @function 为我们生成request对象
 */

public class CommonRequest {

    /**
     * @author BTT
     * @param url
     * @param params
     * @return 返回一个为我们创建好的Request对象
     */
    public static Request createPostRequest(String url,RequestParams params){

        FormBody.Builder mFormBodyBuild = new FormBody.Builder();
        if(params != null){
            for(Map.Entry<String,String>entry :params.urlParams.entrySet()){
                //将请求参数遍历添加到我们的请求构件中
                mFormBodyBuild.add(entry.getKey(),entry.getValue());
            }
        }
        FormBody mFormBody = mFormBodyBuild.build();

        return new Request.Builder().url(url).post(mFormBody).build();
    }

    /**
     *
     * @param url
     * @param params
     * @return 通过传入的参数返回一个get类型的请求
     */
    public static Request createGetRequest(String url,RequestParams params){
        StringBuilder urlBuilder = new StringBuilder(url).append("?");

        if(params != null){
            for(Map.Entry<String,String>entry :params.urlParams.entrySet()){

                urlBuilder.append(entry.getKey()).append("=")
                        .append(entry.getValue()).append("&");
            }
        }

        return new Request.Builder().url(urlBuilder.substring(0,urlBuilder.length()-1))
                .get().build();
    }

    public static Request createMonitorRequest(String url, RequestParams params) {
        StringBuilder urlBuilder = new StringBuilder(url).append("&");
        if (params != null && params.hasParams()) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        return new Request.Builder().url(urlBuilder.substring(0, urlBuilder.length() - 1)).get().build();
    }

}
