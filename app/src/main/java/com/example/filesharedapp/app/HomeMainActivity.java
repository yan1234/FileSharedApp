package com.example.filesharedapp.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.fragment.AppFragment;
import com.example.filesharedapp.app.fragment.MusicFragment;
import com.example.filesharedapp.app.fragment.OtherFragment;
import com.example.filesharedapp.app.fragment.PhotoFragment;
import com.example.filesharedapp.app.fragment.VideoFragment;
import com.example.filesharedapp.app.transfers.SendShowActivity;
import com.example.filesharedapp.app.transfers.TransferShowActivity;
import com.example.filesharedapp.app.transfers.entity.QrcodeInfo;
import com.example.filesharedapp.framework.ui.base.BaseFragment;
import com.example.filesharedapp.utils.json.JsonUtil;
import com.yanling.android.scanlibrary.ScanCameraActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * app首页
 * @author yanling
 * @date 2015-10-15
 */
public class HomeMainActivity extends FragmentActivity implements View.OnClickListener{

    //定义头部的左右操作按钮
    private ImageView header_left;
    private ImageView header_right;

    //定义底部的导航操作
    private View bottom_left;//传输记录
    private View bottom_middle;//传输
    private View bottom_right;//关于我

    //定义viewPager
    private ViewPager mViewPager;
    //定义fragment适配器
    private FragmentPagerAdapter mAdapter;
    //定义fragment集合
    private List<BaseFragment> fragments = new ArrayList<BaseFragment>();
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
        //载入头部按钮布局
        header_left = (ImageView)this.findViewById(R.id.header_left);
        header_right = (ImageView)this.findViewById(R.id.header_right);
        //载入底部布局
        bottom_left = this.findViewById(R.id.home_main_bottom_left);
        bottom_middle = this.findViewById(R.id.home_main_bottom_middle);
        bottom_right = this.findViewById(R.id.home_main_bottom_right);

        //载入基础布局
        mViewPager = (ViewPager)this.findViewById(R.id.viewPager);
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


    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //首页隐藏返回按钮
        header_left.setVisibility(View.INVISIBLE);
        //给右侧的扫描按钮添加点击事件
        header_right.setOnClickListener(this);

        //底部区域操作按钮添加点击事件
        bottom_left.setOnClickListener(this);
        bottom_middle.setOnClickListener(this);
        bottom_right.setOnClickListener(this);

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
            public void onPageSelected(int index) {
                //设置线条样式
                selectPage(index);
                position = index;
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
            //跳转到二维码展示界面，并传递对应的文件信息
            case 1:
                Intent intent = new Intent(HomeMainActivity.this, SendShowActivity.class);
                //封装待发送的文件路径
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileinfos", fragments.get(position).getSelectList());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.home_main_bottom_left:
                //底部左侧的操作栏
                Toast.makeText(HomeMainActivity.this, "你点击了左侧操作栏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.home_main_bottom_middle:
                //底部左侧的操作栏
                Toast.makeText(HomeMainActivity.this, "你点击了中间操作栏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.home_main_bottom_right:
                //底部左侧的操作栏
                Toast.makeText(HomeMainActivity.this, "你点击了右侧操作栏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.header_right:
                //扫描点击事件
                startActivityForResult(new Intent(HomeMainActivity.this, ScanCameraActivity.class), 100);
                break;
            case R.id.tab0:
                selectPage(0);
                position = 0;
                break;
            case R.id.tab1:
                selectPage(1);
                position = 1;
                break;
            case R.id.tab2:
                selectPage(2);
                position = 2;
                break;
            case R.id.tab3:
                selectPage(3);
                position = 3;
                break;
            case R.id.tab4:
                selectPage(4);
                position = 4;
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
                bundle.putSerializable("fileinfos", qrcodeInfo);
                intent.putExtras(bundle);
                //跳转到传输界面
                startActivity(intent);
            }
        }
    }

}
