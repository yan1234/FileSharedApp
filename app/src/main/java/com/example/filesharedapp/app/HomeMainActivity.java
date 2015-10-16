package com.example.filesharedapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.filesharedapp.R;

import java.util.ArrayList;
import java.util.List;


/**
 * app首页
 * @author yanling
 * @date 2015-10-15
 */
public class HomeMainActivity extends FragmentActivity implements View.OnClickListener{

    //定义抽屉布局
    private DrawerLayout mDrawerLayout;
    //定义头部抽屉icon
    private ImageView header_icon;
    //定义viewPager
    private ViewPager mViewPager;
    //定义fragment适配器
    private FragmentPagerAdapter mAdapter;
    //定义fragment集合
    private List<Fragment> fragments = new ArrayList<Fragment>();

    //定义tab切换的头部linearlayout
    private LinearLayout appLayout;
    private LinearLayout photoLayout;
    private LinearLayout musicLayout;
    private LinearLayout videoLayout;
    private LinearLayout otherLayout;
    //定义头部的切换线条
    private View line_tab0;
    private View line_tab1;
    private View line_tab2;
    private View line_tab3;
    private View line_tab4;


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
        //载入基础布局
        mDrawerLayout = (DrawerLayout)this.findViewById(R.id.drawer_layout);
        mViewPager = (ViewPager)this.findViewById(R.id.viewPager);
        header_icon = (ImageView)this.findViewById(R.id.header_icon);
        //载入tab选项卡布局
        appLayout = (LinearLayout)this.findViewById(R.id.tab0);
        photoLayout = (LinearLayout)this.findViewById(R.id.tab1);
        musicLayout = (LinearLayout)this.findViewById(R.id.tab2);
        videoLayout = (LinearLayout)this.findViewById(R.id.tab3);
        otherLayout = (LinearLayout)this.findViewById(R.id.tab4);
        //载入头部的切换线条
        line_tab0 = (View)this.findViewById(R.id.tab0_line);
        line_tab1 = (View)this.findViewById(R.id.tab0_line);
        line_tab2 = (View)this.findViewById(R.id.tab0_line);
        line_tab3 = (View)this.findViewById(R.id.tab0_line);
        line_tab4 = (View)this.findViewById(R.id.tab0_line);



    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //添加头部icon的点击事件
        header_icon.setOnClickListener(this);
        //添加绑定适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //头部icon的点击事件
            case R.id.header_icon:
                //触发抽屉事件
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    }
}
