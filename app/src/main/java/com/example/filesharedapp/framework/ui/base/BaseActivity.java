package com.example.filesharedapp.framework.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.filesharedapp.R;


/**
 * 基类的Activity，主要用来页面公共模块处理，比如统一的标题头
 * @author yanling
 * @date 2015-10-27
 */
public class BaseActivity extends Activity {

    //整个布局view
    protected View main;
    // 加载器
    protected LayoutInflater mInflater;

    //定义头部的界面元素
    //左部的菜单，返回按钮
    protected ImageView header_operate;
    //中间的标题头
    protected TextView header_title;
    //中间的开关按钮
    protected ImageView header_switch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实例化加载器
        mInflater = LayoutInflater.from(this);
    }

    /**
     *重写下资源布局载入方法
     */
    @Override
    public void setContentView(int layoutResID){
        //根据资源文件加载View
        main = mInflater.inflate(layoutResID, null);
        super.setContentView(main);
        init(main);
    }

    /**
     * 初始化基类公共模块
     */
    private void init(View view){
        if (view != null){
            //返回按钮
            header_operate = (ImageView)findViewById(R.id.header_left);
            //标题
            header_title = (TextView)findViewById(R.id.header_title);
            //开关按钮
            header_switch = (ImageView)findViewById(R.id.header_right);
        }
        //绑定操作事件
        if (header_operate != null){
            header_operate.setOnClickListener(new ClickListener());
        }
    }

    /**
     * 设置头部标题
     * @param title
     */
    protected void setTitle(String title){
        header_title.setText(title);
    }

    /**
     * 点击事件
     */
    class ClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                //返回按钮
                case R.id.header_left:
                    break;
            }
        }
    }
}
