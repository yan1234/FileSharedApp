package com.example.filesharedapp.app.transfers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.transfers.entity.QrcodeInfo;
import com.example.filesharedapp.core.SocketInApp;
import com.example.filesharedapp.framework.media.entity.BaseFileInfo;
import com.example.filesharedapp.framework.ui.icon.ResourceManager;
import com.example.filesharedapp.framework.wifi.WifiController;
import com.yanling.android.view.progress.NoticeProgressManager;

/**
 * 文件传输显示列表，主要是显示各个文件上传的一个进度
 * @author yanling
 * @date 2015-10-29
 */
public class TransferShowActivity extends Activity {


    //定义进度列表listview
    private ListView lv_progress;

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
        lv_progress = (ListView)this.findViewById(R.id.activity_transfer_show_list);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //连接上指定的wifi
        wifiController = new WifiController(TransferShowActivity.this);
        //开始接收
        startAccept();
    }

    private void startAccept(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                wifiController.connectWifi(qrcodeInfo.getSsid(), qrcodeInfo.getPreSharedKey());
                //获取ip地址(xxx.xxx.xxx.xxx)
                while (!(wifiController.wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)){
                    try{
                        //100毫秒休眠一次
                        Thread.currentThread();
                        Thread.sleep(100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                String ipAddress = wifiController.getWifiAddress();
                //得到服务端的ip地址(xxx.xxx.xxx.1)
                String remoteIp = ipAddress.substring(0, ipAddress.lastIndexOf(".")) + ".1";
                //开启socket连接
                SocketInApp.startClientSocket(remoteIp, qrcodeInfo, null);
            }
        }).start();
    }


}
