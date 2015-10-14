package com.example.filesharedapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.filesharedapp.app.HomeMainActivity;

import java.util.ArrayList;


/**
 * 欢迎页面
 * @author yanling
 * @date 2015-10-14 17:01
 */
public class WelcomeActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{

    //定义ViewPager对象
    private ViewPager viewPager;
    //定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;
    //定义一个ArrayList来存放View
    private ArrayList<View> views;
    //引导图片资源
    private static final int[] pics = {R.drawable.welcome2,R.drawable.welcome3,R.drawable.welcome4};
    //底部小点的图片
    private ImageView[] points;
    //记录当前选中位置
    private int currentIndex;
    //记录页面最后位置
    private int lastIntoCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        //初始化界面
        initView();
        //初始化数据
        initData();
    }

    /**
     * 初始化组件
     */
    private void initView(){
        //实例化ArrayList对象
        views = new ArrayList<View>();
        //实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        //定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);

        //初始化引导图片列表
        for(int i=0; i<pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setBackgroundResource(pics[i]);
            views.add(iv);
        }
        //设置数据
        viewPager.setAdapter(vpAdapter);
        //设置监听
        viewPager.setOnPageChangeListener(this);
        //初始化底部小点
        initPoint();
    }

    /**
     * 初始化底部小点
     */
    private void initPoint(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

        points = new ImageView[pics.length];

        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            //得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            //默认都设为灰色
            points[i].setEnabled(true);
            //给每个小点设置监听
            points[i].setOnClickListener(this);
            //设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        //设置当面默认的位置
        currentIndex = 0;
        //设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 当滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
        switch (arg0) {
            case 0: // 停止变更
                if (viewPager.getCurrentItem() == pics.length - 1) {
                    lastIntoCount = lastIntoCount + 1;
                }
                break;
            case 1://正在变更
                break;
            case 2: // 已经变更
                lastIntoCount = 0;
                break;
        }
        if (lastIntoCount > 1) {
            if (arg0 == 0) {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this,
                        HomeMainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.home_enter_in, R.anim.home_enter_out);
            }
        }
    }



    /**
     * 当当前页面被滑动时调用
     */

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * 当新的页面被选中时调用
     */

    @Override
    public void onPageSelected(int position) {
        //设置底部小点选中状态
        setCurDot(position);
    }

    /**
     * 通过点击事件来切换当前的页面
     */
    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position){
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon){
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }


    /**
     * @ClassName: ViewPagerAdapter
     * @Description: TODO(viewpager适配器)
     * @author fenglei015@deppon.com/273219
     * @date 2015-9-18 下午4:59:44
     *
     */
    class ViewPagerAdapter extends PagerAdapter {

        //界面列表
        private ArrayList<View> views;

        public ViewPagerAdapter (ArrayList<View> views){
            this.views = views;
        }

        /**
         * 获得当前界面数
         */
        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }
            return 0;
        }

        /**
         * 初始化position位置的界面
         */
        @Override
        public Object instantiateItem(View view, int position) {
            if (position == pics.length - 1) {

                //判断当前页面为最后一页时添加监听事件，进行页面跳转
                views.get(position).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        intent.setClass(WelcomeActivity.this,
                                HomeMainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.home_enter_in, R.anim.home_enter_out);
                    }
                });
            }

            ((ViewPager) view).addView(views.get(position), 0);

            return views.get(position);
        }

        /**
         * 判断是否由对象生成界面
         */
        @Override
        public boolean isViewFromObject(View view, Object arg1) {
            return (view == arg1);
        }

        /**
         * 销毁position位置的界面
         */
        @Override
        public void destroyItem(View view, int position, Object arg2) {
            ((ViewPager) view).removeView(views.get(position));
        }
    }

}
