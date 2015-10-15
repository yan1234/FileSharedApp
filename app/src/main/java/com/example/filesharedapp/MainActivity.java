package com.example.filesharedapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.example.filesharedapp.app.HomeMainActivity;


/**
 * 启动页
 * @author yanling
 * @date 2015-10-14 16:26
 */
public class MainActivity extends Activity {

    //定义变量标志程序第一次安装启动，0：第一次启动；1：已经启动过
    private int welcome_first;
    //定义是否是第一次安装保存状态
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置去掉标题头
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //得到sp对象
        sp = getSharedPreferences("welcome", this.MODE_WORLD_WRITEABLE);
        //得到是否第一次安装的标志
        welcome_first = sp.getInt("welcome_first", 0);

        //启动页面停留2s后直接跳转到主页
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //程序第一次运行，跳转到欢迎页
                if (welcome_first == 0) {
                    //得到sp编辑对象并设置标志变量
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("welcome_first", 1);
                    editor.commit();
                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    MainActivity.this.finish();
                }//直接跳转到主页
                else if (welcome_first == 1) {
                    startActivity(new Intent(MainActivity.this, HomeMainActivity.class));
                    MainActivity.this.finish();
                }

            }
        }, 2000);
    }

}
