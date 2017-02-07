package com.yanling.fileshared.app.transfers.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.yanling.android.scanlibrary.ScanUtils;
import com.yanling.fileshared.app.transfers.android.SendBetweenAppActivity;
import com.yanling.fileshared.app.transfers.android.entity.QrcodeInfo;
import com.yanling.fileshared.framework.Constants;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;
import com.yanling.fileshared.framework.storage.StorageManager;
import com.yanling.fileshared.framework.wifi.WifiController;
import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;
import com.yanling.socket.SocketManager;

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


    private static final String TAG = TransferService.class.getSimpleName();

    //传输类别常量
    private static final int TYPE_TRANSFER_TO_OTHER_PHONE = 0; //发送到其他手机
    private static final int TYPE_TRANSFER_FROM_OTHER_PHONE = 1; //从其他手机接收
    private static final int TYPE_TRANSFER_TO_PC = 2; //发送到电脑
    private static final int TYPE_TRANSFER_FROM_PC = 3; //从电脑接收


    //定义标志保存当前服务是否已经在运行
    private boolean isRunning = false;

    //定义随机生成的Socket端口号(50000-59999之间)
    private int socket_port;

    //定义当前的传输类别
    private int type_transfer;

    //定义条码信息对象
    private QrcodeInfo qrcodeInfo;

    //定义待发送文件列表对象
    private List<BaseFileInfo> list;

    //定义socket服务端
    private ServerSocket server;

    //定义连接到的客户端的数量
    private int client_count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        //随机生成socket端口号
        socket_port = new Random().nextInt(10000) + 50000;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "调用传输服务");
        //判断当前服务的运行状况
        if (isRunning){
            //当前服务已经在运行，跳转到指定的界面
        }else {
            //获取当前的传输类别
            type_transfer = intent.getExtras().getInt(Constants.BUNDLE_KEY_TRANSFER_TYPE);
            //表示和手机间互传
            if (type_transfer == TYPE_TRANSFER_TO_OTHER_PHONE
                    || type_transfer == TYPE_TRANSFER_FROM_OTHER_PHONE){
                Log.d(TAG, "手机间互传");
                transferBetweenPhone(intent);
            }//电脑间互传
            else if (type_transfer == TYPE_TRANSFER_TO_PC
                    || type_transfer == TYPE_TRANSFER_FROM_PC){
                Log.d(TAG, "与电脑互传");
                transferBetweenPC(intent);
            }
            isRunning = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 手机间互传
     * @param data，传递的bundle信息
     */
    private void transferBetweenPhone(Intent data){
        //表示是向其他手机发送文件
        if (type_transfer == TYPE_TRANSFER_TO_OTHER_PHONE){
            //获取待发送的文件列表
            list = (List<BaseFileInfo>)data.getExtras().getSerializable(Constants.BUNDLE_KEY_TRANSFER);
            //封装条码信息
            qrcodeInfo = new QrcodeInfo();
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
                new TransferBetweenPhoneThread().start();
                //生成二维码并展示
                Intent intent = new Intent(TransferService.this, SendBetweenAppActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.BUNDLE_KEY_QRCODEINFO, qrcodeInfo);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                Toast.makeText(TransferService.this, "无线共享开启失败", Toast.LENGTH_SHORT).show();
            }
        }else{
            //从其他手机接收文件
            //获取文件条码信息
            qrcodeInfo = (QrcodeInfo)data.getExtras().getSerializable(Constants.BUNDLE_KEY_QRCODEINFO);
            //开启客户端用于接收文件
            new TransferBetweenPhoneThread().start();
        }
    }

    /**
     * 手机与PC互传
     * @param data，传递的bundle信息
     */
    private void transferBetweenPC(Intent data){

    }


    /**
     * 手机间互传线程
     */
    class TransferBetweenPhoneThread extends Thread{
        @Override
        public void run() {
            super.run();
            try{
                if (type_transfer == TYPE_TRANSFER_TO_OTHER_PHONE){
                    //发送文件，开启socket服务端
                    startSend();
                }else{
                    //接收文件，开启socket客户端
                    startAccept();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }

        /**
         * 开始发送
         */
        private void startSend() throws IOException{
            //启动socket服务端
            server = new ServerSocket(socket_port);
            while (!server.isClosed()) {
                //获取连接客户端
                Socket socket = server.accept();
                SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler("SERVER_APP",
                        socket, new TransferSocketCallback(),
                        SimpleSocketHandler.FLAG_HANDLER_OUT);
                File[] files = new File[list.size()];
                for (int i = 0; i < files.length; i++) {
                    File file = new File(list.get(i).getPath());
                    files[i] = file;
                }
                simpleSocketHandler.setFiles(files);
                //开启服务端
                SocketManager.getInstance().start(simpleSocketHandler);
            }
        }

        /**
         * 开始接收
         */
        private void startAccept() throws IOException{
            Log.d(TAG, "开始接收");
            //连接上指定的wifi
            WifiController wifiController = new WifiController(TransferService.this);
            wifiController.connectWifi(qrcodeInfo.getSsid(), qrcodeInfo.getPreSharedKey());
            try {
                //休眠3s然后再获取连接的ip地址
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String ipAddress = wifiController.getWifiAddress();
            Log.d(TAG, "连接wifiip地址为：" + ipAddress);
            //得到服务端的ip地址(xxx.xxx.xxx.1)
            String remoteIp = ipAddress.substring(0, ipAddress.lastIndexOf(".")) + ".1";
            Log.d(TAG, "远端ip地址为：" + remoteIp);
            //开启socket连接
            Socket socket = new Socket(remoteIp, qrcodeInfo.getHostPort());
            //处理socket消息
            SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler(
                    "ClientMain", socket, new TransferSocketCallback(), SimpleSocketHandler.FLAG_HANDLER_IN
            );
            //设置接收文件存储路径
            simpleSocketHandler.rootDir = StorageManager.getInstance().getDownload().getPath() + File.separator;
            //开始接收
            new Thread(simpleSocketHandler).start();
        }
    }

    /**
     * 实现传输进度的socket回调
     */
    class TransferSocketCallback implements SocketCallback{

        @Override
        public void start(String name, long totalSize, boolean isIn) {
            Log.d("SocketCallback", "开始传输：" + name);
        }

        @Override
        public void handlerProgress(String name, long totalSize, long transSize, boolean isIn) {
            Log.d("SocketCallback", "进度值：" + transSize * 100/totalSize);
        }

        @Override
        public void end(String name, boolean isIn) {
            Log.d("SocketCallback", "结束传输：" + name);
        }

        @Override
        public void error(Exception e) {

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
