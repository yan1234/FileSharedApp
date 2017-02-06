package com.yanling.fileshared.app.transfers.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.yanling.android.scanlibrary.ScanUtils;
import com.yanling.fileshared.app.transfers.android.SendBetweenAppActivity;
import com.yanling.fileshared.app.transfers.android.entity.QrcodeInfo;
import com.yanling.fileshared.framework.Constants;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;
import com.yanling.fileshared.framework.wifi.WifiController;
import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;

/**
 * 传输服务
 * @author yanling
 * @date 2017-02-06
 */
public class TransferService extends Service{

    //定义标志保存当前服务是否已经在运行
    private boolean isRunning = false;

    //定义随机生成的Socket端口号(50000-59999之间)
    private int socket_port;

    //定义待发送文件列表对象
    private List<BaseFileInfo> list;

    //定义socket服务端
    private ServerSocket server;

    @Override
    public void onCreate() {
        super.onCreate();
        //随机生成socket端口号
        socket_port = new Random().nextInt(10000) + 50000;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //判断当前服务的运行状况
        if (isRunning){
            //当前服务已经在运行，跳转到指定的界面
        }else{
            //启动服务
        }
        //判断当前是哪种传输类型
        int type = intent.getExtras().getInt("");
        switch (type){
            case 1:
        }
        //向其他android手机发送文件
        sendToPhone(intent);



        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 向其他Android手机发送文件
     * @param intent, 待发送的文件列表
     */
    private void sendToPhone(Intent intent){
        //获取文件信息
        list = (List<BaseFileInfo>)intent.getExtras().getSerializable(Constants.BUNDLE_KEY_TRANSFER);
        //封装条码信息
        QrcodeInfo qrcodeInfo = new QrcodeInfo();
        qrcodeInfo.setHostPort(socket_port);
        qrcodeInfo.setFiles(list);
        //设置wifi的名称和密码
        String ssid = Constants.WIFI_NAME + socket_port;
        qrcodeInfo.setSsid(ssid);
        qrcodeInfo.setPreSharedKey(ssid);
        WifiController wifiController = new WifiController(TransferService.this);
        if (wifiController.startWifiAp(ssid, ssid, true)){
            //wifi开启成功
            //开启socket服务端用于发送文件
            new SendThread().start();
            //生成二维码并展示
            Intent data = new Intent(TransferService.this, SendBetweenAppActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.BUNDLE_KEY_QRCODEINFO, qrcodeInfo);
            data.putExtras(bundle);
            data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(data);
        }else{
            Toast.makeText(TransferService.this, "无线共享开启失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送文件线程
     */
    class SendThread extends Thread{
        @Override
        public void run() {
            super.run();
            //开启socket服务端
            //开启socket连接
            try {
                server = new ServerSocket(socket_port);
                while (!server.isClosed()){
                    //获取连接客户端
                    Socket socket = server.accept();
                    SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler("SERVER_APP",
                            socket,
                            new SocketCallback() {
                                @Override
                                public void start(String name, long totalSize, boolean isIn) {
                                    Log.d("SocketServer", "开始传输：" + name);
                                }

                                @Override
                                public void handlerProgress(String name, long totalSize, long transSize, boolean isIn) {
                                    Log.d("SocketServer", "进度值：" + transSize * 100/totalSize);
                                }

                                @Override
                                public void end(String name, boolean isIn) {
                                    Log.d("SocketServer", "结束传输：" + name);
                                }

                                @Override
                                public void error(Exception e) {

                                }
                            },
                            SimpleSocketHandler.FLAG_HANDLER_OUT);
                    File[] files = new File[list.size()];
                    for (int i=0; i < files.length; i++){
                        File file = new File(list.get(i).getPath());
                        files[i] = file;
                    }
                    simpleSocketHandler.setFiles(files);
                    new Thread(simpleSocketHandler).start();
                    //开启服务端
                    //SocketManager.getInstance().start(simpleSocketHandler);
                    Log.d("SocketServer", "开启服务端");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
