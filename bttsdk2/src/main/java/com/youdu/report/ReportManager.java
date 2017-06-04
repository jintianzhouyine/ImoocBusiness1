package com.youdu.report;

import com.youdu.module.monitor.Monitor;
import com.youdu.okhttp.CommonOkHttpClient;
import com.youdu.okhttp.HttpConstant;
import com.youdu.okhttp.listener.DisposeDataHandle;
import com.youdu.okhttp.listener.DisposeDataListener;
import com.youdu.okhttp.request.CommonRequest;
import com.youdu.okhttp.request.RequestParams;
import com.youdu.util.Utils;

import java.util.ArrayList;

/**
 * Created by mycomputer on 2017/5/1.
 */

public class ReportManager {

    /**
     * 默认的事件回调处理
     */
    private static DisposeDataHandle handle = new DisposeDataHandle(
            new DisposeDataListener() {
                @Override
                public void onSuccess(Object responseObj) {
                }

                @Override
                public void onFailure(Object reasonObj) {
                }
            });

    public static void sueReport(ArrayList<Monitor> monitors, boolean isFull,long playTime){
        if (monitors != null && monitors.size()>0){
            for(Monitor monitor : monitors){
                RequestParams params = new RequestParams();
                if(Utils.containString(monitor.url,HttpConstant.ATM_PRE)){
                    if(isFull){
                        params.put("fu","1");
                    }
                    params.put("ve",String.valueOf(playTime));
                }
                /**
                 * 调用封装好的网络开发组件发送检测请求
                 */
                CommonOkHttpClient.get(CommonRequest.createMonitorRequest(monitor.url,params),handle);
            }
        }
    }

    /**
     * send the su report
     */
    public static void suReport(ArrayList<Monitor> monitors, long playTime) {
        if (monitors != null && monitors.size() > 0) {
            for (Monitor monitor : monitors) {
                RequestParams params = new RequestParams();
                if (monitor.time == playTime) {
                    if (Utils.containString(monitor.url, HttpConstant.ATM_PRE)) {
                        params.put("ve", String.valueOf(playTime));
                    }
                    CommonOkHttpClient.get(CommonRequest.createMonitorRequest(monitor.url, params), handle);
                }
            }
        }
    }
}
