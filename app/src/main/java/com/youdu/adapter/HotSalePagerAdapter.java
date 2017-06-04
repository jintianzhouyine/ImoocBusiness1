package com.youdu.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mycomputer.imoocbusiness1.R;
import com.youdu.imageloader.ImageLoaderManger;
import com.youdu.module.recommand.RecommandBodyValue;

import java.util.ArrayList;

/**
 * Created by mycomputer on 2017/4/4.
 */

public class HotSalePagerAdapter extends PagerAdapter{
    private Context mContext;
    private LayoutInflater mInflate;

    private ArrayList<RecommandBodyValue> mData;
    private ImageLoaderManger mImageLoader;

    public HotSalePagerAdapter(Context context,ArrayList<RecommandBodyValue> list){
        mContext = context;
        mData = list;
        mInflate = LayoutInflater.from(mContext);
        mImageLoader = ImageLoaderManger.getInstance(mContext);
    }

    @Override
    public int getCount() {

        return Integer.MAX_VALUE;//这里返回一个比较大的数，可实现无限循环
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final RecommandBodyValue value = mData.get(position%mData.size());//这里讲取值始终固定0~mData.size()
        /**
         * 初始化控件
         */
        View rootView = mInflate.inflate(R.layout.item_hot_product_pager_layout, null);
        TextView titleView = (TextView) rootView.findViewById(R.id.title_view);
        TextView infoView = (TextView) rootView.findViewById(R.id.info_view);
        TextView gonggaoView = (TextView) rootView.findViewById(R.id.gonggao_view);
        TextView saleView = (TextView) rootView.findViewById(R.id.sale_num_view);
        ImageView[] imageViews = new ImageView[3];
        imageViews[0] = (ImageView) rootView.findViewById(R.id.image_one);
        imageViews[1] = (ImageView) rootView.findViewById(R.id.image_two);
        imageViews[2] = (ImageView) rootView.findViewById(R.id.image_three);

        /**
         * 绑定数据到View
         */
        titleView.setText(value.title);
        infoView.setText(value.price);
        gonggaoView.setText(value.info);
        saleView.setText(value.text);
        for (int i = 0; i < imageViews.length; i++) {
            mImageLoader.display(imageViews[i], value.url.get(i));
        }
        container.addView(rootView, 0);
        return rootView;
    }
}
