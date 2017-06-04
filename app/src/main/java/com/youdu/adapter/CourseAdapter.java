package com.youdu.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import com.example.mycomputer.imoocbusiness1.R;
import com.youdu.core.VideoAdContext;
import com.youdu.imageloader.ImageLoaderManger;
import com.youdu.module.recommand.RecommandBodyValue;
import com.youdu.util.Util;
import com.youdu.util.Utils;


import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mycomputer on 2017/4/3.
 */

public class CourseAdapter extends BaseAdapter {

    /**
     * ListView 不同类型的Item所做的标示
     */
    private static final int CARD_COUNT =4;
    private static final int VIDOE_TYPE = 0x00;
    private static final int CARD_SIGNAL_PIC = 0x02;
    private static final int CARD_MULTI_PIC = 0x01;
    private static final int CARD_VIEW_PAGER = 0x03;

    private Context mContext;
    private ViewHolder mViewHolder;
    private LayoutInflater mInflate;
    private VideoAdContext mVideoAdContext;

    private ArrayList<RecommandBodyValue> mData;
    /**
     * 异步图片加载工具类
     */
    private ImageLoaderManger mImageLoader;

    /**
     * 构造方法
     * @param context
     * @param data
     */

    public CourseAdapter(Context context,ArrayList<RecommandBodyValue> data){
        mContext = context;
        mData = data;
        mInflate = LayoutInflater.from(mContext);
        mImageLoader = ImageLoaderManger.getInstance(mContext);
    }

    /**
     * 返回列表的大小
     * @return
     */
    @Override
    public int getCount() {
        return mData.size();
    }

    /**
     * 获取item
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        return CARD_COUNT;
    }

    /**
     * 通知Adapter使用哪种类型的Item去加载数据
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        RecommandBodyValue value = (RecommandBodyValue) getItem(position);
        return value.type;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //1.获取数据的Type类型
        int type = getItemViewType(position);
        final RecommandBodyValue value = (RecommandBodyValue)getItem(position);
        //为空表明当前没有可使用的缓存View
        if(convertView == null){
            switch (type){
                case CARD_SIGNAL_PIC:
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_product_card_one_layout, parent, false);
                    //初始化ViewHolder中所有的控件
                    mViewHolder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    mViewHolder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    mViewHolder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
                    mViewHolder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    mViewHolder.mProductView = (ImageView) convertView.findViewById(R.id.product_photo_view);
                    mViewHolder.mPriceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    mViewHolder.mFromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    mViewHolder.mZanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    break;

                case CARD_MULTI_PIC:
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_product_card_two_layout,parent,false);
                    //初始化多图ViewHolder中的View
                    mViewHolder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    mViewHolder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    mViewHolder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
                    mViewHolder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    mViewHolder.mPriceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    mViewHolder.mFromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    mViewHolder.mZanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    mViewHolder.mProductLayout = (LinearLayout) convertView.findViewById(R.id.product_photo_layout);
                    break;
                case CARD_VIEW_PAGER:
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_product_card_three_layout,parent,false);
                    mViewHolder.mViewPager = (ViewPager)convertView.findViewById(R.id.pager);
                    mViewHolder.mViewPager.setPageMargin(Utils.dip2px(mContext,12));
                    //为viewPager填充数据
                    ArrayList<RecommandBodyValue> recommandList = Util.handleData(value);
                    mViewHolder.mViewPager.setAdapter(new HotSalePagerAdapter(mContext,recommandList));
                    //一开始就让我们的Viewpager处于比较靠中间的项目
                    mViewHolder.mViewPager.setCurrentItem(recommandList.size()*100);
                    break;
                case VIDOE_TYPE:
                    //显示video卡片
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_video_layout, parent, false);
                    mViewHolder.mVieoContentLayout = (RelativeLayout)
                            convertView.findViewById(R.id.video_ad_layout);
                    mViewHolder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    mViewHolder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    mViewHolder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
                    mViewHolder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    mViewHolder.mShareView = (ImageView) convertView.findViewById(R.id.item_share_view);
                    //为对应布局创建播放器
                    mVideoAdContext = new VideoAdContext(mViewHolder.mVieoContentLayout,
                            new Gson().toJson(value));
                    break;
            }
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder)convertView.getTag();
        }

        //开始绑定数据
        switch(type){
            case CARD_SIGNAL_PIC:
                mViewHolder.mTitleView.setText(value.title);
                mViewHolder.mInfoView.setText(value.info.concat(mContext.getString(R.string.tian_qian)));
                mViewHolder.mFooterView.setText(value.text);
                mViewHolder.mPriceView.setText(value.price);
                mViewHolder.mFromView.setText(value.from);
                mViewHolder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(value.zan));
                /**
                 * 为ImageView加载图片
                 */
                mImageLoader.display(mViewHolder.mLogoView,value.logo);
                mImageLoader.display(mViewHolder.mProductView,value.url.get(0));

                break;
            case CARD_MULTI_PIC:
                //为我们的多图Item绑定数据
                mImageLoader.display(mViewHolder.mLogoView, value.logo);
                mViewHolder.mTitleView.setText(value.title);
                mViewHolder.mInfoView.setText(value.info.concat(mContext.getString(R.string.tian_qian)));
                mViewHolder.mFooterView.setText(value.text);
                mViewHolder.mPriceView.setText(value.price);
                mViewHolder.mFromView.setText(value.from);
                mViewHolder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(value.zan));

                //动态的添加ImageView到水平scrollViwe中
                mViewHolder.mProductLayout.removeAllViews();//删除已有的图片
                for(String url : value.url){
                    mViewHolder.mProductLayout.addView(createImageView(url));
                }
                break;


        }
        return convertView;
    }

    /**
     * 调用API层的方法
     */
    public void updateAdInScrollView(){
        if(mVideoAdContext != null){
            mVideoAdContext.updateVideoInScrollView();
        }
    }

    /**
     * 动态的创建ImageView
     * @return
     */
    private ImageView createImageView(String url){
        ImageView imageView = new ImageView(mContext);
        //与要添加的viewGroup保持一致
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(mContext,100),LinearLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = Utils.dip2px(mContext,5);
        imageView.setLayoutParams(params);
        mImageLoader.display(imageView,url);
        return imageView;
    }
    /**
     * 用来缓存我们已经创建好的Item
     */
    private static class ViewHolder{
        //所有Card共有的属性
        private CircleImageView mLogoView;
        private TextView mTitleView;
        private TextView mInfoView;
        private TextView mFooterView;
        //VideoCard特有的属性
        private RelativeLayout mVieoContentLayout;
        private ImageView mShareView;

        //Video Card外所有的Card具有属性
        private TextView mPriceView;
        private TextView mFromView;
        private TextView mZanView;
        //Card One特有属性
        private LinearLayout mProductLayout;
        //Card Two特有属性
        private ImageView mProductView;
        //Card Three特有属性
        private ViewPager mViewPager;

    }


}
