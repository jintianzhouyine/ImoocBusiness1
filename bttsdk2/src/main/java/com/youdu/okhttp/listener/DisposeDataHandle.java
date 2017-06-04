package com.youdu.okhttp.listener;

/**
 * Created by mycomputer on 2017/3/31.
 */

public class DisposeDataHandle {
    public DisposeDataListener mListener = null;
    public Class<?> mClass = null;

    public DisposeDataHandle(DisposeDataListener listener){
        this.mListener = listener;
    }

    public DisposeDataHandle(DisposeDataListener listener,Class<?> clazz){
        this.mListener = listener;
        this.mClass = clazz;
    }
}
