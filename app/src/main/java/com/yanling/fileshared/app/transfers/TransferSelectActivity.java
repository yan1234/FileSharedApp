package com.yanling.fileshared.app.transfers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yanling.fileshared.R;
import com.yanling.fileshared.framework.ui.base.BaseActivity;

/**
 * 传输模式选择界面
 * @author yanling
 * @date 2017-01-12
 */
public class TransferSelectActivity extends BaseActivity implements
        View.OnClickListener{

    //android服务端
    private Button btn_select_android;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_select);
        //初始化界面
        initView();
        //初始化事件
        initEvent();
    }

    private void initView(){
        btn_select_android = (Button)this.findViewById(R.id.activity_transfer_select_android);
    }

    private void initEvent(){
        //添加点击事件
        btn_select_android.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_transfer_select_android:
                //点击开启android服务端
                break;
        }
    }
}
