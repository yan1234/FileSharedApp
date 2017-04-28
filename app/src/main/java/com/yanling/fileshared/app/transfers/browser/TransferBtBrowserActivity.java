package com.yanling.fileshared.app.transfers.browser;

import android.os.Bundle;
import android.widget.TextView;

import com.yanling.fileshared.R;
import com.yanling.fileshared.app.transfers.android.entity.QrcodeInfo;
import com.yanling.fileshared.framework.Constants;
import com.yanling.fileshared.framework.ui.base.BaseActivity;

/**
 * 通过浏览器进行收发文件
 * @author yanling
 * @date 2017-04-11
 */
public class TransferBtBrowserActivity extends BaseActivity{

    //定义3个显示控件
    private TextView tv_connect_wifi;
    private TextView tv_download_url;
    private TextView tv_upload_url;

    //定义传输数据对象
    private QrcodeInfo qrcodeInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_browser);
        if (getIntent() != null && getIntent().getExtras() != null){
            qrcodeInfo = (QrcodeInfo) getIntent().getExtras().getSerializable(Constants.BUNDLE_KEY_QRCODEINFO);
        }
        //初始化界面和事件
        initView();
        initEvent();
    }

    private void initView(){
        //设置标题
        setTitle("浏览器传输");
        tv_connect_wifi = (TextView)this.findViewById(R.id.activity_browser_tv_connect_wifi);
        tv_download_url = (TextView)this.findViewById(R.id.activity_browser_tv_download);
        tv_upload_url = (TextView)this.findViewById(R.id.activity_browser_tv_upload);

        //设置参数
        tv_connect_wifi.setText("名称: " + qrcodeInfo.getSsid()
                + ", 密码: " + qrcodeInfo.getPreSharedKey());
        tv_download_url.setText("http://192.168.43.1:"+qrcodeInfo.getHostPort() + "/download.html");
        tv_upload_url.setText("http://192.168.43.1:"+qrcodeInfo.getHostPort() + "/upload.bak.html");
    }

    private void initEvent(){

    }
}
