package com.youdu.okhttp.listener;

/**
 * Created by mycomputer on 2017/3/31.
 */

public interface DisposeDataListener {
    /**
     * 请求成功回调事件
     * @param responseObj
     */
    public void onSuccess(Object responseObj);

    /**
     * 请求失败回调事件
     * @param reasonObj
     */
    public void onFailure(Object reasonObj);
}
