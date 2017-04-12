package com.yanling.fileshared.app;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yanling.android.dialog.popdialog.bottommenu.BottomMenuDialog;
import com.yanling.android.scanlibrary.ScanCameraActivity;
import com.yanling.fileshared.R;
import com.yanling.fileshared.app.main.AppFragment;
import com.yanling.fileshared.app.main.MusicFragment;
import com.yanling.fileshared.app.main.OtherFragment;
import com.yanling.fileshared.app.main.PhotoFragment;
import com.yanling.fileshared.app.main.VideoFragment;
import com.yanling.fileshared.app.transfers.TransferInitActivity;
import com.yanling.fileshared.app.transfers.android.entity.QrcodeInfo;
import com.yanling.fileshared.app.transfers.service.TransferService;
import com.yanling.fileshared.framework.Constants;
import com.yanling.fileshared.framework.ui.base.BaseFragment;
import com.yanling.fileshared.utils.json.JsonUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * app首页
 * @author yanling
 * @date 2015-10-15
 */
public class HomeMainActivity extends FragmentActivity
        implements View.OnClickListener, View.OnLongClickListener{

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

        /**
         * 设置传输服务长按取消事件
         */
        bottom_middle.setOnLongClickListener(this);

        //根据传输服务运行的状态判断显示的图标
        if (TransferService.isRunning){
            ((ImageView)bottom_middle).setImageResource(R.mipmap.bg_transfer);
        }

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
        //设置左右缓存的页面数量
        mViewPager.setOffscreenPageLimit(1);
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
            case R.id.home_main_bottom_left:
                //底部左侧的操作栏
                Toast.makeText(HomeMainActivity.this, "你点击了左侧操作栏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.home_main_bottom_middle:
                //底部中间的操作栏
                showTransferDialog();
                break;
            case R.id.home_main_bottom_right:
                //底部右侧的操作栏
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

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.home_main_bottom_middle:
                //弹出关闭服务的弹框
                showCloseTransferDialog();
                break;
        }
        return false;
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
                Log.d("HomeMainActivity", ">>>扫描条码为:"+code);
                //将字符串信息转化为传输实体
                QrcodeInfo qrcodeInfo = JsonUtil.jsonToObject(code, QrcodeInfo.class);
                //封装信息传输到服务
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BUNDLE_KEY_TRANSFER_TYPE, 1);
                bundle.putSerializable(Constants.BUNDLE_KEY_QRCODEINFO, qrcodeInfo);
                //开启服务
                startTransferService(bundle);
            }
        }
    }

    /**
     * 展示传输类别选择弹框
     */
    private void showTransferDialog(){
        //构造选择器弹框
        final BottomMenuDialog dialog = new BottomMenuDialog();
        final String[] items = {"发送文件到其他Android手机", "接收文件从其他Android手机", "发送文件到电脑", "接收文件从电脑"};
        dialog.setMenuItems(HomeMainActivity.this, items);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                //表示需要发送文件到其他端（手机或者电脑）
                if (i == 0 || i == 2) {
                    //判断是否选中了文件
                    if (fragments.get(position).getSelectList().size() >= 1) {
                        //封装待发送的文件路径
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.BUNDLE_KEY_TRANSFER_TYPE, i);
                        bundle.putSerializable(Constants.BUNDLE_KEY_TRANSFER, fragments.get(position).getSelectList());
                        startTransferService(bundle);
                    } else {
                        Toast.makeText(HomeMainActivity.this, "请至少选择一个待发送的文件", Toast.LENGTH_SHORT).show();
                    }
                }//表示从其他手机扫码接收文件
                else if (i == 1){
                    startActivityForResult(new Intent(HomeMainActivity.this, ScanCameraActivity.class), 100);
                }//表示从电脑端接收文件
                else if (i == 3){
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.BUNDLE_KEY_TRANSFER_TYPE, i);
                    //开启服务
                    startTransferService(bundle);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
        /*//构造选择器弹框
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeMainActivity.this);
        builder.setTitle("请选择传输的类型");
        //设置列表
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //表示需要发送文件到其他端（手机或者电脑）
                if (i == 0 || i == 2) {
                    //判断是否选中了文件
                    if (fragments.get(position).getSelectList().size() >= 1) {
                        //封装待发送的文件路径
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.BUNDLE_KEY_TRANSFER_TYPE, i);
                        bundle.putSerializable(Constants.BUNDLE_KEY_TRANSFER, fragments.get(position).getSelectList());
                        startTransferService(bundle);
                    } else {
                        Toast.makeText(HomeMainActivity.this, "请至少选择一个待发送的文件", Toast.LENGTH_SHORT).show();
                    }
                }//表示从其他手机扫码接收文件
                else if (i == 1){
                    startActivityForResult(new Intent(HomeMainActivity.this, ScanCameraActivity.class), 100);
                }//表示从电脑端接收文件
                else if (i == 3){
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.BUNDLE_KEY_TRANSFER_TYPE, i);
                    //开启服务
                    startTransferService(bundle);
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();*/

    }

    /**
     * 展示关闭服务的确认弹框
     */
    private void showCloseTransferDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeMainActivity.this);
        builder.setTitle("确定要关闭传输服务吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //关闭服务
                stopTransferService();
                //调整图标
                ((ImageView)bottom_middle).setImageResource(R.mipmap.bg_upload);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //关闭弹框
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 开启传输服务
     * @param bundle，代传递的bundle信息
     */
    private void startTransferService(Bundle bundle){
        Intent intent = new Intent(HomeMainActivity.this, TransferInitActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 关闭传输服务
     */
    private void stopTransferService(){
        Intent intent = new Intent(HomeMainActivity.this, TransferService.class);
        stopService(intent);
    }

}
