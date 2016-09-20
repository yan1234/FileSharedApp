package com.example.filesharedapp.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.fragment.AppFragment;
import com.example.filesharedapp.app.fragment.MusicFragment;
import com.example.filesharedapp.app.fragment.OtherFragment;
import com.example.filesharedapp.app.fragment.PhotoFragment;
import com.example.filesharedapp.app.fragment.VideoFragment;
import com.example.filesharedapp.app.transfers.SendShowActivity;
import com.example.filesharedapp.app.transfers.TransferShowActivity;
import com.example.filesharedapp.app.transfers.entity.QrcodeInfo;
import com.example.filesharedapp.utils.json.JsonUtil;
import com.example.scanlibrary.ScanCameraActivity;

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
    //定义当前页面的位置
    private int position = 0;

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

    //定义悬浮的快捷操作按钮
    private ImageView home_shortcut_show;
    private ImageView home_shortcut_cancel;
    private ImageView home_shortcut_send;
    private ImageView home_shortcut_accept;
    //定义遮挡的蒙层
    private RelativeLayout home_shortcut_frame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        //初始化view
        initView();
        //初始化事件
        initEvent();
        //设置初始的页面为图片页
        selectPage(1);
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
        line_tab1 = (View)this.findViewById(R.id.tab1_line);
        line_tab2 = (View)this.findViewById(R.id.tab2_line);
        line_tab3 = (View)this.findViewById(R.id.tab3_line);
        line_tab4 = (View)this.findViewById(R.id.tab4_line);
        //添加fragment
        AppFragment appFragment = new AppFragment();
        PhotoFragment photoFragment = new PhotoFragment();
        MusicFragment musicFragment = new MusicFragment();
        VideoFragment videoFragment = new VideoFragment();
        OtherFragment otherFragment = new OtherFragment();
        fragments.add(appFragment);
        fragments.add(photoFragment);
        fragments.add(musicFragment);
        fragments.add(videoFragment);
        fragments.add(otherFragment);

        //添加快捷按钮和遮挡蒙层
        home_shortcut_show = (ImageView)findViewById(R.id.home_shortcut_show);
        home_shortcut_cancel = (ImageView)findViewById(R.id.home_shortcut_cancel);
        home_shortcut_send = (ImageView)findViewById(R.id.home_shortcut_send);
        home_shortcut_accept = (ImageView)findViewById(R.id.home_shortcut_accept);
        home_shortcut_frame = (RelativeLayout)findViewById(R.id.home_shortcut_frame);

    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //添加头部icon的点击事件
        header_icon.setOnClickListener(this);
        //添加快捷按钮的点击事件
        home_shortcut_show.setOnClickListener(this);
        home_shortcut_cancel.setOnClickListener(this);
        home_shortcut_send.setOnClickListener(this);
        home_shortcut_accept.setOnClickListener(this);
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
        //为viewPager添加页面改变事件
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //设置线条样式
                selectPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //给每个选项卡设置点击事件
        appLayout.setOnClickListener(this);
        photoLayout.setOnClickListener(this);
        musicLayout.setOnClickListener(this);
        videoLayout.setOnClickListener(this);
        otherLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //头部icon的点击事件
            case R.id.header_icon:
                //触发抽屉事件
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.home_shortcut_show:
                //展现蒙层
                home_shortcut_frame.setVisibility(View.VISIBLE);
                break;
            case R.id.home_shortcut_cancel:
                //关闭蒙层
                home_shortcut_frame.setVisibility(View.GONE);
                break;
            //跳转到二维码展示界面，并传递对应的文件信息
            case R.id.home_shortcut_send:
                Intent intent = new Intent(HomeMainActivity.this, SendShowActivity.class);
                //封装待发送的文件路径
                //.....
                startActivity(intent);
                home_shortcut_frame.setVisibility(View.GONE);
                break;
            //扫描二维码
            case R.id.home_shortcut_accept:
                startActivityForResult(new Intent(HomeMainActivity.this, ScanCameraActivity.class),
                        100);
                home_shortcut_frame.setVisibility(View.GONE);
                break;
            case R.id.tab0:
                selectPage(0);
                break;
            case R.id.tab1:
                selectPage(1);
                break;
            case R.id.tab2:
                selectPage(2);
                break;
            case R.id.tab3:
                selectPage(3);
                break;
            case R.id.tab4:
                selectPage(4);
                break;

        }
    }

    /**
     * 根据位置设置显示的页面
     * @param position，初始位置从0开始
     */
    private void selectPage(int position){
        //首先重置底部图片
        resetImage();
        //根据position设置ViewPager的当前页面
        mViewPager.setCurrentItem(position);
        switch (position){
            case 0:
                line_tab0.setVisibility(View.VISIBLE);
                break;
            case 1:
                line_tab1.setVisibility(View.VISIBLE);
                break;
            case 2:
                line_tab2.setVisibility(View.VISIBLE);
                break;
            case 3:
                line_tab3.setVisibility(View.VISIBLE);
                break;
            case 4:
                line_tab4.setVisibility(View.VISIBLE);
                break;
        }
        //记录当前fragment的位置
        this.position = position;

    }

    /**
     * 隐藏其他tab的线条
     */
    private void resetImage() {
        // TODO Auto-generated method stub
        line_tab0.setVisibility(View.INVISIBLE);
        line_tab1.setVisibility(View.INVISIBLE);
        line_tab2.setVisibility(View.INVISIBLE);
        line_tab3.setVisibility(View.INVISIBLE);
        line_tab4.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //表示进行二维码扫描
        if (requestCode == 100){
            //表示扫描成功
            if (resultCode == ScanCameraActivity.SCAN_OK){
                //获取扫描得到的二维码
                String code = data.getExtras().getString(ScanCameraActivity.SCAN_CODE);
                //将字符串信息转化为传输实体
                QrcodeInfo qrcodeInfo = JsonUtil.jsonToObject(code, QrcodeInfo.class);
                //传递信息到文件发送页面
                Intent intent = new Intent(HomeMainActivity.this, TransferShowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("QrcodeInfo", qrcodeInfo);
                intent.putExtras(bundle);
                //跳转到传输界面
                startActivity(intent);
            }
        }
    }

}
