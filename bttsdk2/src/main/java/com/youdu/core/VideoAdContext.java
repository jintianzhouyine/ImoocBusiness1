package com.youdu.core;

import android.view.ViewGroup;

import com.google.gson.Gson;
import com.youdu.module.AdValue;

/**
 * Created by mycomputer on 2017/5/1.
 */

public class VideoAdContext implements VideoAdSlot.AdSDKSlotListener {

    //the ad content
    private ViewGroup mParentView;

    private VideoAdSlot mAdSlot;
    private AdValue mInstance = null;

    public VideoAdContext(ViewGroup parentView,String instance){

        mParentView = parentView;
        mInstance = (AdValue) new Gson().fromJson(instance,AdValue.class);
        load();
    }

    /**
     * 创建slot业务逻辑类，不调用则不会创建最底层的CustomVideoView
     */
    private void load() {
        if(mInstance != null && mInstance.resource != null){
            mAdSlot = new VideoAdSlot(mInstance,this);
        }else {
            mAdSlot = new VideoAdSlot(null,this);
        }
    }

    public void updateVideoInScrollView(){
        if(mAdSlot != null){
            mAdSlot.updateVideoInScrollView();
        }
    }


    @Override
    public ViewGroup getAdParent() {
        return mParentView;
    }

    @Override
    public void onAdVideoLoadSuccess() {

    }

    @Override
    public void onAdVideoLoadFailed() {
    }

    @Override
    public void onAdVideoLoadComplete() {
    }

    @Override
    public void onClickVideo(String url) {
    }
}
