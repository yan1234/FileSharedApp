package com.yanling.fileshared.app.transfers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.yanling.android.view.progress.NumberProgressView;
import com.yanling.fileshared.R;
import com.yanling.fileshared.app.transfers.android.entity.QrcodeInfo;
import com.yanling.fileshared.app.transfers.service.TransferService;
import com.yanling.fileshared.framework.Constants;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;
import com.yanling.fileshared.framework.ui.base.BaseActivity;
import com.yanling.fileshared.framework.ui.dialog.LoadingDialog;
import com.yanling.fileshared.framework.wifi.WifiController;

import java.util.List;
import java.util.Random;

/**
 * 传输服务初始化界面
 *
 * @author yanling
 * @date 2017-03-14
 */
public class TransferInitActivity extends BaseActivity{

    //定义传输类别
    private int transfer_type;

    //定义传递给传输服务的信息
    private QrcodeInfo qrcodeInfo;

    //定义加载弹框
    private LoadingDialog dialog;

    //定义回调对象
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //热点开启/wifi连接成功
                    startTransferService();
                    TransferInitActivity.this.finish();
                    break;
                case -1:
                    //热点开启失败
                    Toast.makeText(TransferInitActivity.this, "开启wifi热点失败，请重试", Toast.LENGTH_SHORT).show();
                    TransferInitActivity.this.finish();
                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_init);
        dialog = new LoadingDialog(TransferInitActivity.this).setTitle("正在启动服务...");
        dialog.show();
        //获取传输类别
        Bundle bundle = getIntent().getExtras();
        transfer_type = bundle.getInt(Constants.BUNDLE_KEY_TRANSFER_TYPE);
        qrcodeInfo = new QrcodeInfo();
        //随机生成端口号
        qrcodeInfo.setHostPort(new Random().nextInt(10000) + 50000);
        execOperate(bundle);
    }

    /**
     * 根据传输类别执行对应的操作
     * @param bundle
     */
    private void execOperate(Bundle bundle){
        //根据transfer_type传输类别执行相应的操作
        switch (transfer_type){
            //发送文件到其他手机
            case TransferService.TYPE_TRANSFER_TO_OTHER_PHONE:
                //获取待发送的文件列表
                List<BaseFileInfo> list = (List<BaseFileInfo>)bundle.getSerializable(Constants.BUNDLE_KEY_TRANSFER);
                //封装文件列表信息
                qrcodeInfo.setFiles(list);
                //开启wifi热点
                //设置wifi的名称和密码
                String ssid = Constants.WIFI_NAME + qrcodeInfo.getHostPort();
                qrcodeInfo.setSsid(ssid);
                qrcodeInfo.setPreSharedKey(ssid);
                //开启线程执行wifi热点开启操作
                startWifiAp();
                break;
            //从其他手机接收文件
            case TransferService.TYPE_TRANSFER_FROM_OTHER_PHONE:
                //获取扫描得到的待接收的文件信息
                qrcodeInfo = (QrcodeInfo)bundle.getSerializable(Constants.BUNDLE_KEY_QRCODEINFO);
                //连接上指定的wifi
                //开启线程执行wifi连接操作
                startWifiConnect();
                break;
            //发送文件到浏览器
            case TransferService.TYPE_TRANSFER_TO_PC:
                //获取待发送的文件列表
                List<BaseFileInfo> list1 = (List<BaseFileInfo>)bundle.getSerializable(Constants.BUNDLE_KEY_TRANSFER);
                qrcodeInfo.setFiles(list1);
                //开启服务
                startTransferService();
                this.finish();
                break;
            //从浏览器接收文件
            case TransferService.TYPE_TRANSFER_FROM_PC:
                //直接开启服务
                startTransferService();
                this.finish();
                break;
        }
    }

    /**
     * 开启传输服务
     */
    private void startTransferService(){
        //封装完成信息后开启服务
        Intent intent = new Intent(TransferInitActivity.this, TransferService.class);
        Bundle data = new Bundle();
        //封装传输类别
        data.putInt(Constants.BUNDLE_KEY_TRANSFER_TYPE, transfer_type);
        //封装传输信息
        data.putSerializable(Constants.BUNDLE_KEY_QRCODEINFO, qrcodeInfo);
        intent.putExtras(data);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null){
            dialog.dismiss();
        }
    }

    /**
     * 热点开启操作
     */
    private void startWifiAp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开热点前开启wifi连接,以便达到禁用热点的功能
                WifiController wifiController = new WifiController(TransferInitActivity.this);
                //关闭wifi连接
                wifiController.setWifiStatus(true);
                //休眠1s
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //开启wifi热点
                if (wifiController.startWifiAp(qrcodeInfo.getSsid(), qrcodeInfo.getPreSharedKey(), true)){
                    //开启成功,
                    //发送回调消息
                    handler.sendEmptyMessage(0);
                }else{
                    //失败回调消息
                    handler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }

    /**
     * wifi连接操作
     */
    private void startWifiConnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //连接wifi前先关闭wifi连接
                WifiController wifiController = new WifiController(TransferInitActivity.this);
                wifiController.setWifiStatus(false);
                //休眠
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //定义标志变量保存是否连接上指定的wifi
                boolean isConnect = false;
                while (!isConnect){
                    wifiController.connectWifi(qrcodeInfo.getSsid(), qrcodeInfo.getPreSharedKey());
                    //连接成功后获取当前连接的wifi名称是否是指定的wifi名称
                    if (wifiController.isWifiConnected()
                            && wifiController.getWifiInfo() != null
                            && wifiController.getWifiInfo().getSSID().contains(qrcodeInfo.getSsid())){
                        //这里要注意获取的SSID会多一对引号""SSID""
                        isConnect = true;
                    }
                }
                //这里连接上wifi后等待一会,便于后面的wifi连接地址的获取
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //连接成功后发送成功回调
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

}
