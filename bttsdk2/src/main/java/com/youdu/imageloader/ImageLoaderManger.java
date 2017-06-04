package com.youdu.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.bttsdk.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by mycomputer on 2017/4/3.
 * @function 初始化UniverImageLoader
 */

public class ImageLoaderManger {

    private static final int THREAD_COUNT = 4; //最多可以同时开启几条线程
    private static final int PROPRITY = 2;//加载图片的优先级
    private static final int DISK_CACHE_SIZE = 50*1024;//缓存的硬盘大小
    private static final int CONNECTION_TIME_OUT = 5*1000;//链接的超时时间
    private static final int READ_TIME_OUT = 30*1000;//读取超时时间

    private static ImageLoader mImageLoader = null;
    private static ImageLoaderManger mInstance = null;
    private DisplayImageOptions defultOptions;

    public ImageLoaderManger(Context context) {

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(context)
                .threadPoolSize(THREAD_COUNT)
                .threadPriority(Thread.NORM_PRIORITY - PROPRITY)//由于Android系统的差异，我们不知道系统的优先级设置，
                // 所以要先获取系统的标准优先级然后降两级
                .denyCacheImageMultipleSizesInMemory()//防止缓存多套尺寸图片到我们的内存
                .memoryCache(new WeakMemoryCache())//使用弱引用内存缓存，在系统内存不足的时候可以回收图片缓存的空间
                .diskCacheSize(DISK_CACHE_SIZE)//分配硬盘缓存大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//使用MD5 命名文件
                .tasksProcessingOrder(QueueProcessingType.LIFO)//图片下载顺序
                .defaultDisplayImageOptions(getDefultOptions())//默认的图片加载option
                .imageDownloader(new BaseImageDownloader(context,CONNECTION_TIME_OUT,READ_TIME_OUT))//设置图片加载器
                .writeDebugLogs()//debug模式下会输出日志
                .build();

        ImageLoader.getInstance().init(configuration);
        mImageLoader = ImageLoader.getInstance();
    }

    /**
     * 实现默认的Options
     * @return
     */
    public DisplayImageOptions getDefultOptions() {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.xadsdk_img_error)//图片地址为空的时候
                .showImageOnFail(R.drawable.xadsdk_img_error)//图片下载失败的时候显示的图片
                .cacheInMemory(true)//设置图片可以缓存在内存
                .cacheOnDisk(true)//设置图片可以缓存在硬盘
                .bitmapConfig(Bitmap.Config.RGB_565)//使用的解码类型
                .decodingOptions(new BitmapFactory.Options())//图片解码配置
                .build();

        return options;
    }

    /**
     * 加载图片的api
     * @param imageView
     * @param url
     * @param options
     * @param listener
     */

    public void displayImage(ImageView imageView, String url, DisplayImageOptions options, ImageLoadingListener listener){
        if(mImageLoader != null){
            mImageLoader.displayImage(url,imageView,options,listener);
        }
    }

    public void display(ImageView imageView, String url, DisplayImageOptions options, ImageLoadingListener listener){
        displayImage(imageView,url,options,listener);
    }
    public void display(ImageView imageView, String url, ImageLoadingListener listener){
        displayImage(imageView,url,null,listener);
    }

    public void display(ImageView imageView, String url){
        displayImage(imageView,url,null,null);
    }

    public static ImageLoaderManger getInstance(Context context) {
        if(mInstance==null){
            synchronized (ImageLoaderManger.class){
                if(mInstance==null){

                    mInstance = new ImageLoaderManger(context);
                }
            }
        }

        return mInstance;
    }


}
