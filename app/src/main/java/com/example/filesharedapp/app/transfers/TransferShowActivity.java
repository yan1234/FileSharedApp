package com.example.filesharedapp.app.transfers;

import android.app.Activity;
import android.os.Bundle;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.transfers.entity.QrcodeInfo;
import com.example.filesharedapp.core.SocketInApp;
import com.example.filesharedapp.framework.wifi.WifiController;

/**
 * 文件传输显示列表，主要是显示各个文件上传的一个进度
 * @author yanling
 * @date 2015-10-29
 */
public class TransferShowActivity extends Activity {

    //定义条码实体信息对象
    private QrcodeInfo qrcodeInfo = null;

    //定义wifi控制对象
    private WifiController wifiController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_show);
        //得到二维码扫描到的条码信息
        qrcodeInfo = (QrcodeInfo)getIntent().getExtras().getSerializable("fileinfos");
        initView();
        initEvent();
    }

    /**
     * 初始化界面
     */
    private void initView(){

    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //连接上指定的wifi
        wifiController = new WifiController(TransferShowActivity.this);
        wifiController.connectWifi(qrcodeInfo.getSsid(), qrcodeInfo.getPreSharedKey());
        //开始接收
        startAccept();
    }

    private void startAccept(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //模拟等待10s钟
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //获取ip地址(xxx.xxx.xxx.xxx)
                if (wifiController.isWifiConnected()){
                    String ipAddress = wifiController.getWifiAddress();
                    //得到服务端的ip地址(xxx.xxx.xxx.1)
                    String remoteIp = ipAddress.substring(0, ipAddress.lastIndexOf(".")) + ".1";
                    //开启socket连接
                    SocketInApp.startClientSocket(remoteIp, qrcodeInfo);
                }
            }
        }).start();
    }


}
