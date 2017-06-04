package com.youdu.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by mycomputer on 2017/3/31.
 */

public class ImageLoaderTest {
    public Context context;
    public ImageView imageView;
    private void testApi(){
        /**
         * 为我们配置Imageloader的参数
         */
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(context).build();

        /**
         * 先来获取到imageloader的实例
         */
        ImageLoader imageLoader = ImageLoader.getInstance();

        /**
         * 为我们显示图片的时候进行配置
         */
        DisplayImageOptions options = new DisplayImageOptions.Builder().build();

        /**
         * 使用displayImage去加载图片
         */
        imageLoader.displayImage("url",imageView,options,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
            }
        });

    }
}
