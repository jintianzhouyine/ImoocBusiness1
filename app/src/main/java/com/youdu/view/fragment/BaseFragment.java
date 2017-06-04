package com.youdu.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by mycomputer on 2017/3/29.
 * @fuction 主要为所有的fragment提供公共的功能和组件
 */

public class BaseFragment extends Fragment {
    protected Activity mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
