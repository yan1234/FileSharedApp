package com.example.filesharedapp.app.send;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.framework.ui.base.BaseActivity;
import com.example.scanlibrary.ScanUtils;

public class SendShowActivity extends BaseActivity {

    //二维码图片
    private ImageView image_qrcode;
    //提示文字
    private TextView text_tip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_show);
        //初始化界面
        initView();
        //初始化事件；
        initEvent();
    }

    /**
     * 初始化界面
     */
    private void initView(){
        //设置标题头
        setTitle("扫描传输");
        image_qrcode = (ImageView)findViewById(R.id.send_image_qrcode);
        text_tip = (TextView)findViewById(R.id.send_text_tips);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //生成二维码并展示
        String content = "测试数据";
        image_qrcode.setImageBitmap(ScanUtils.encodeQRCode(content,
                250, 250));
        text_tip.setText("文件："+content);
    }
}
