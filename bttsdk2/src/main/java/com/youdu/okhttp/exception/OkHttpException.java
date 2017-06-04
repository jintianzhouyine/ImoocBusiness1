package com.youdu.okhttp.exception;

/**
 * Created by mycomputer on 2017/3/31.
 * @function 自定义异常类
 */

public class OkHttpException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * the server return code
     */
    private int ecode;

    /**
     * the server return error message
     */
    private Object emsg;

    public OkHttpException(int ecode,Object emsg){
        this.ecode =ecode;
        this.emsg = emsg;
    }

    public int getEcode(){return ecode;}

    public Object getEmsg(){return emsg;}
}
