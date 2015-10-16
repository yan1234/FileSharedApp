package com.example.filesharedapp.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.filesharedapp.R;

/**
 * Created by 272388 on 2015/10/16.
 */
public class MainContentFragment extends Fragment implements View.OnClickListener{

    //定义整个页面的view
    private View view;
    //定义侧滑布局
    private DrawerLayout mDrawerLayout;
    //头部侧滑icon
    private ImageView header_icon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载主页Fragment页面布局
        view = inflater.inflate(R.layout.home_main_content, null);
        //加载界面
        initView();
        //加载事件
        initEvent();
        return view;
    }

    public MainContentFragment(){

    }

    @SuppressLint("ValidFragment")
    public MainContentFragment(DrawerLayout mDrawerLayout){
        this.mDrawerLayout = mDrawerLayout;
    }

    /**
     * 加载界面
     */
    private void initView(){
        header_icon = (ImageView)view.findViewById(R.id.header_icon);
    }
    /**
     * 初始化事件
     */
    private void initEvent(){
        header_icon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //头部侧滑icon点击事件
            case R.id.header_icon:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    }
}
