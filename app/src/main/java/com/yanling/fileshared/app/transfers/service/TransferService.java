package com.yanling.fileshared.app.transfers.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.yanling.fileshared.app.transfers.android.SendBetweenAppActivity;
import com.yanling.fileshared.app.transfers.android.entity.QrcodeInfo;
import com.yanling.fileshared.app.transfers.common.ProgressActivity;
import com.yanling.fileshared.app.transfers.common.ProgressEntity;
import com.yanling.fileshared.framework.Constants;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;
import com.yanling.fileshared.framework.storage.StorageManager;
import com.yanling.fileshared.framework.wifi.WifiController;
import com.yanling.socket.HttpSocketHandler;
import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;
import com.yanling.socket.SocketManager;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * 传输服务
 * @author yanling
 * @date 2017-02-06
 */
public class TransferService extends Service{


    private static final String TAG = TransferService.class.getSimpleName();

    //传输类别常量
    public static final int TYPE_TRANSFER_TO_OTHER_PHONE = 0; //发送到其他手机
    public static final int TYPE_TRANSFER_FROM_OTHER_PHONE = 1; //从其他手机接收
    public static final int TYPE_TRANSFER_TO_PC = 2; //发送到电脑
    public static final int TYPE_TRANSFER_FROM_PC = 3; //从电脑接收


    //定义标志保存当前服务是否已经在运行
    public static boolean isRunning = false;

    //定义当前的传输类别
    private int type_transfer;

    //定义传输信息
    private QrcodeInfo qrcodeInfo;


    //定义socket服务端
    private ServerSocket server;
    //定义socket客户端
    private Socket socket;

    //定义map保存当前连接到的所有客户端的传输进度(key值为当前连接的是第几个client)
    private List<EventMessageForClient> clients = new ArrayList<>();
    //定义连接到的客户端的数量
    private int client_count = 0;

    //定义当前service运行的线程对象
    private Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent == null){
            return START_NOT_STICKY;
        }
        //判断是否是初次启动service
        if (!isRunning){
            //初次启动需要对操作进行初始化
            //获取传输类别和传输信息
            type_transfer = intent.getExtras().getInt(Constants.BUNDLE_KEY_TRANSFER_TYPE);
            qrcodeInfo = (QrcodeInfo) intent.getExtras().getSerializable(Constants.BUNDLE_KEY_QRCODEINFO);

            if (type_transfer == TYPE_TRANSFER_TO_OTHER_PHONE
                    || type_transfer == TYPE_TRANSFER_FROM_OTHER_PHONE){
                //开启手机端互传socket
                Log.d(TAG, "手机端互传");
                thread = new TransferBetweenPhoneThread();
                thread.start();
            }else{
                //开启手机与PC端互传socket
                Log.d(TAG, "手机与PC端互传");
                thread = new TransferBetweenPCThread();
                thread.start();
            }

        }
        showTransfer();
        isRunning = true;
        return START_NOT_STICKY;
    }

    /**
     * 跳转到指定的传输展示界面
     */
    private void showTransfer(){
        //根据类别跳转指定界面
        switch (type_transfer){
            //跳转到条码展示界面供其他终端扫描
            case TYPE_TRANSFER_TO_OTHER_PHONE:
                Intent intent = new Intent(TransferService.this, SendBetweenAppActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.BUNDLE_KEY_QRCODEINFO, qrcodeInfo);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            //跳转到进度展示界面
            case TYPE_TRANSFER_FROM_OTHER_PHONE:
                Intent intent1 = new Intent(TransferService.this, ProgressActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                break;
            //跳转到与浏览器交互界面
            case TYPE_TRANSFER_TO_PC:
            case TYPE_TRANSFER_FROM_PC:
                Intent intent2 = new Intent(TransferService.this, SendBetweenAppActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable(Constants.BUNDLE_KEY_QRCODEINFO, qrcodeInfo);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
        }
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
                onError(e);
            }

        }

        /**
         * 开始发送
         */
        private void startSend() throws IOException{
            //启动socket服务端
            server = new ServerSocket(qrcodeInfo.getHostPort());
            while (!server.isClosed()) {
                //获取连接客户端
                Socket socket = server.accept();
                client_count ++;
                //定义当前连接客户端的标记
                String tag = "Client" + client_count;
                SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler(tag + client_count,
                        socket, new TransferSocketCallback(tag),
                        SimpleSocketHandler.FLAG_HANDLER_OUT);
                File[] files = new File[qrcodeInfo.getFiles().size()];
                for (int i = 0; i < files.length; i++) {
                    File file = new File(qrcodeInfo.getFiles().get(i).getPath());
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
            String ipAddress = wifiController.getWifiAddress();
            //得到服务端的ip地址(xxx.xxx.xxx.1)
            String remoteIp = ipAddress.substring(0, ipAddress.lastIndexOf(".")) + ".1";
            Log.d(TAG, "远端ip地址为：" + remoteIp);
            //开启socket连接
            socket = new Socket(remoteIp, qrcodeInfo.getHostPort());
            //处理socket消息
            String tag = "Client_App";
            SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler(
                    tag, socket, new TransferSocketCallback(tag), SimpleSocketHandler.FLAG_HANDLER_IN
            );
            //设置接收文件存储路径
            simpleSocketHandler.rootDir = StorageManager.getInstance().getDownload().getPath() + File.separator;
            //开始接收
            simpleSocketHandler.run();
        }
    }

    /**
     * 手机与浏览器间互传线程
     */
    class TransferBetweenPCThread extends Thread{
        @Override
        public void run() {
            super.run();
            try{
                //启动socket服务端
                server = new ServerSocket(qrcodeInfo.getHostPort());
                while (!server.isClosed()){
                    //获取连接的客户端
                    Socket socket = server.accept();
                    client_count ++;
                    //定义当前连接客户端的标记
                    String tag = "Client" + client_count;
                    HttpSocketHandler httpSocketHandler = new HttpSocketHandler(tag + client_count,
                            socket, new TransferSocketCallback(tag));
                    File[] files = new File[qrcodeInfo.getFiles().size()];
                    for (int i = 0; i < files.length; i++) {
                        File file = new File(qrcodeInfo.getFiles().get(i).getPath());
                        files[i] = file;
                    }
                    //设置待下载的文件列表
                    httpSocketHandler.setFiles(files);
                    //设置上传目录保存路径
                    httpSocketHandler.rootDir = StorageManager.getInstance().getUpload().getPath();
                    //开启服务端
                    SocketManager.getInstance().start(httpSocketHandler);
                }
            }catch (IOException e){
                e.printStackTrace();
                onError(e);
            }
        }

    }

    /**
     * 实现传输进度的socket回调
     */
    class TransferSocketCallback implements SocketCallback{

        //定义列表保存进度值
        private List<ProgressEntity> list = new ArrayList<>();

        //定义当前连接客户端的tag标记
        private String tag;

        @Override
        public void start(String name, long totalSize, boolean isIn) {
            Log.d("SocketCallback", "开始传输：" + name);
            //这里表示新的数据传输
            ProgressEntity entity = new ProgressEntity();
            entity.setTitle(name);
            entity.setTotalSize(totalSize);
            entity.setTag("" + System.currentTimeMillis());
            list.add(entity);
            //发布订阅消息到更新界面
            EventBus.getDefault().post(clients);

        }

        @Override
        public void handlerProgress(String name, long totalSize, long transSize, boolean isIn) {
            Log.d("SocketCallback", "进度值：" + transSize * 100/totalSize);
            //由于下载没有实现多线程，所以每次更新的都是最后一个进度
            list.get(list.size()-1).setDownloadSize(transSize);
            //发布订阅消息
            EventBus.getDefault().post(clients);
        }

        @Override
        public void end(String name, boolean isIn) {
            Log.d("SocketCallback", "结束传输：" + name);
        }

        @Override
        public void error(Exception e) {
            onError(e);
        }

        public TransferSocketCallback(String tag){
            this.tag = tag;
            //将当前客户端添加到列表中
            EventMessageForClient client = new EventMessageForClient();
            client.setTag(tag);
            client.setList(list);
            clients.add(client);
            if (type_transfer == TYPE_TRANSFER_TO_OTHER_PHONE){
                //下面是服务端回调展示处理
                //将消息传递到指定的界面
                EventBus.getDefault().post(client);
            }
        }
    }

    /**
     * 服务运行错误处理
     * @param e
     */
    private void onError(Exception e){
        Log.e(TAG, e.getMessage());
        //关闭服务
        close();
    }

    /**
     * 关闭服务
     */
    private void close(){
        try{
            //关闭socket服务端或者客户端
            if (server != null && !server.isClosed()){
                server.close();
            }
            if (socket != null && !socket.isClosed()){
                socket.close();
            }
            //中断创建的线程
            if (thread != null){
                thread.interrupt();
            }
            //关闭线程池
            SocketManager.getInstance().close();
        }catch (IOException e){
            e.printStackTrace();
        }
        isRunning = false;
        //关闭服务
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭
        close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
