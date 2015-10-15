package com.example.filesharedapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.filesharedapp.R;


/**
 * app首页
 * @author yanling
 * @date 2015-10-15
 */
public class HomeMainActivity extends Activity implements View.OnClickListener{

    //定义抽屉布局
    private DrawerLayout mDrawerLayout;
    //定义头部的icon
    private ImageView header_icon;
    //定义按钮的点击事件
    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        //初始化view
        initView();
        //初始化事件
        initEvent();
    }

    /**
     * 初始化view
     */
    private void initView(){
        mDrawerLayout = (DrawerLayout)this.findViewById(R.id.drawer_layout);
        header_icon = (ImageView)this.findViewById(R.id.header_icon);
        btn = (Button)this.findViewById(R.id.btn);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //初始化icon图标点击的抽屉事件
        header_icon.setOnClickListener(this);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //添加头部图标的点击事件
            case R.id.header_icon:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    }
}
