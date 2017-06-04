package com.youdu.view.fragment.home;

import com.example.mycomputer.imoocbusiness1.R;
import com.youdu.adapter.CourseAdapter;
import com.youdu.module.recommand.BaseRecommandModel;
import com.youdu.network.http.RequestCenter;
import com.youdu.okhttp.listener.DisposeDataListener;
import com.youdu.view.fragment.BaseFragment;
import com.youdu.view.home.HomeHeaderLayout;
import com.youdu.zxing.app.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import javax.security.auth.login.LoginException;

import static android.content.ContentValues.TAG;

/**
 * Created by mycomputer on 2017/3/29.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemClickListener {
    private static final int REQUEST_QRCODE = 0x01;

    /**
     * UI
     */
    private View mContentView;
    private ListView mListView;
    private TextView mCategoryView;
    private TextView mSearchView;
    private ImageView mLoadingView;
    private TextView mScan;

    /**
     * data
     */
    private BaseRecommandModel mRecommandData;
    private CourseAdapter mAdapter;


    public HomeFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestReconnandData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_home_layout,container,false);
        initView();
        return mContentView;
    }

    private void initView() {
        mScan = (TextView)mContentView.findViewById(R.id.qrcode_view);
        mScan.setOnClickListener(this);
        mCategoryView = (TextView) mContentView.findViewById(R.id.category_view);
        mCategoryView.setOnClickListener(this);
        mSearchView = (TextView) mContentView.findViewById(R.id.search_view);
        mSearchView.setOnClickListener(this);
        mListView = (ListView) mContentView.findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        mLoadingView = (ImageView) mContentView.findViewById(R.id.loading_view);
        //启动loadingView动画
        AnimationDrawable anim = (AnimationDrawable) mLoadingView.getDrawable();
        anim.start();
    }

    /**
     * 发送首页列表数据请求
     */
    public void requestReconnandData(){
        RequestCenter.requestRecommandData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                //获取到数据后更新我们的UI
                Toast.makeText(mContext, "数据接收完成", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onSuccess:"+responseObj.toString());
                mRecommandData =(BaseRecommandModel)responseObj;
                showSuccessView();
            }

            @Override
            public void onFailure(Object reasonObj) {

                Toast.makeText(mContext, "数据接收失败", Toast.LENGTH_SHORT).show();
                //提示用户网络有问题
                Log.e(TAG, "onFailure:"+reasonObj.toString());
            }
        });
    }

    /**
     * 请求成功执行的方法
     */
    private void showSuccessView(){
        //判断数据是否为空
        if(mRecommandData.data.list !=null && mRecommandData.data.list.size()>0){
            Toast.makeText(mContext, "数据解析完成", Toast.LENGTH_SHORT).show();
            mLoadingView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mListView.addHeaderView(new HomeHeaderLayout(mContext,mRecommandData.data.head));
            mAdapter = new CourseAdapter(mContext,mRecommandData.data.list);
            mListView.setAdapter(mAdapter);
            /**
             * 为ListView添加滑动事件的监听
             */
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    mAdapter.updateAdInScrollView();
                }
            });
        }else{
            Toast.makeText(mContext, "数据解析失败", Toast.LENGTH_SHORT).show();
            showErrorView();
        }
    }

    /**
     * 请求失败执行的方法
     */
    private void showErrorView(){
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.qrcode_view:
                Intent intent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(intent,REQUEST_QRCODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_QRCODE:
                if(resultCode == Activity.RESULT_OK){
                    String code = data.getStringExtra("SCAN_RESULT");
                    /**
                     * 得到code后，判断是不是一个连接，如果是则使用外置浏览器打开该网页
                     * 如果不是使用吐司弹出扫描结果
                     */
                    if(code.contains("http://")||code.contains("https://")){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(code));
                        startActivity(intent);

                    }else{
                        Toast.makeText(mContext, "扫描结果："+code, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
