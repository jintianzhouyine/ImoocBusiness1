package com.youdu.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.youdu.okhttp.exception.OkHttpException;
import com.youdu.okhttp.listener.DisposeDataHandle;
import com.youdu.okhttp.listener.DisposeDataListener;

import org.json.JSONObject;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by mycomputer on 2017/3/31.
 */

public class CommonJsonCallback implements Callback {

    //与服务器返回的字段的对应关系
    protected final String RESULT_CODE = "ecode";
    protected final int RESULT_CODE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";

    /**
     * 自定义异常类型
     */
    protected final int NETWORK_ERROR = -1;
    protected final int JSON_ERROR = -2;
    protected final int OTHER_ERROR = -3;

    private Handler mDeliveryHandler; //进行消息的转发
    private DisposeDataListener mListener;
    private Class<?> mClass;

    public CommonJsonCallback(DisposeDataHandle handle){
        this.mListener = handle.mListener;
        this.mClass=handle.mClass;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 请求失败处理
     * @param call
     * @param ioexception
     */
    @Override
    public void onFailure(Call call,final IOException ioexception) {

        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR,ioexception));
            }
        });
    }

    @Override
    public void onResponse(final Call call,final Response response) throws IOException {

        final String result= response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {

                handleResponse(result);
            }
        });
    }

    /**
     * 处理服务器返回的响应数据
     * @param responseObj
     */
    private void handleResponse(Object responseObj) {

        if(responseObj == null && responseObj.toString().trim().equals("")){
            mListener.onFailure(new OkHttpException(NETWORK_ERROR,EMPTY_MSG));
            return;
        }

        try {
            JSONObject result = new JSONObject(responseObj.toString());
            //开始尝试解析json
            if(result.has(RESULT_CODE)){
                //从json对象中取出我们的响应码，如果为0则是正确的响应
                if(result.getInt(RESULT_CODE) == RESULT_CODE_VALUE){
                    if(mClass == null){
                        //不需要解析，直接返回数据
                        mListener.onSuccess(responseObj);
                    }else {
                        //需要我们将json对象转化为实体对象
                        Object obj =new Gson().fromJson(responseObj.toString(),mClass);
                        if(obj != null){
                            mListener.onSuccess(obj);
                        }else {
                            mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
                        }
                    }
                }else{
                    mListener.onFailure(new OkHttpException(OTHER_ERROR,result.get(RESULT_CODE)));
                }
            }
        }catch (Exception e){
            mListener.onFailure(new OkHttpException(OTHER_ERROR,e.getMessage()));
        }
    }
}
