package com.yanling.fileshared.framework.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.yanling.fileshared.R;

import org.w3c.dom.Text;

/**
 * 定义加载对话框
 *　@author yanling
 * @date 2017-03-14
 */
public class LoadingDialog extends Dialog{


    //定义对话框标题
    private TextView tv_title;


    public LoadingDialog(Context context) {
        super(context);
        //设置对话框透明
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置对话框布局
        setContentView(R.layout.dialog_loading);
        tv_title = (TextView)findViewById(R.id.dialog_loading_title);
        setCanceledOnTouchOutside(false);
    }

    public LoadingDialog setTitle(String title){
        tv_title.setText(title);
        return this;
    }

}
