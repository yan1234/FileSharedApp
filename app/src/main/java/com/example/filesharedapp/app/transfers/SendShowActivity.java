package com.example.filesharedapp.app.transfers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filesharedapp.R;
import com.example.filesharedapp.framework.media.entity.BaseFileInfo;
import com.example.filesharedapp.app.transfers.entity.QrcodeInfo;
import com.example.filesharedapp.core.SocketInApp;
import com.example.filesharedapp.framework.ui.base.BaseActivity;
import com.example.filesharedapp.framework.wifi.WifiController;
import com.example.filesharedapp.utils.json.JsonUtil;
import com.example.filesharedapp.utils.md5.MD5Utils;
import com.yanling.android.scanlibrary.ScanUtils;
import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;
import com.yanling.socket.SocketManager;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SendShowActivity extends BaseActivity {

    //定义常量标志wifi名称
    private static final String WIFI_NAME = "FileShare";

    //二维码图片
    private ImageView image_qrcode;
    //提示文字
    private TextView text_tip;

    //定义待发送的条码信息对象
    private QrcodeInfo qrcodeInfo = null;
    //定义待发送的文件列表对象
    private List<BaseFileInfo> baseFileInfoList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_show);
        //获取上一个界面传递过来的待发送的文件列表
        baseFileInfoList = (List<BaseFileInfo>)getIntent().getExtras().getSerializable("fileinfos");
        //初始化界面
        initView();
        //初始化事件；
        initEvent();


        //测试代码
        ((Button)findViewById(R.id.btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Button)findViewById(R.id.btn)).setText("关闭传输");
                startSend();
            }
        });
        ((Button)findViewById(R.id.accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Button)findViewById(R.id.accept)).setText("关闭接收");
                startAccept();
            }
        });
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
        //初始化socket 端口号
        qrcodeInfo = new QrcodeInfo();
        //随机生成端口号(50000-59999之间)
        qrcodeInfo.setHostPort(50000 + new Random().nextInt(10000));
        //开启wifi
        //随机生成wifi名称和密码
        String ssid = WIFI_NAME + new Random().nextInt(100);
        String password = ssid;
        qrcodeInfo.setSsid(ssid);
        qrcodeInfo.setPreSharedKey(password);
        WifiController wifiController = new WifiController(SendShowActivity.this);
        if (wifiController.startWifiAp(ssid, password, true)){
            //wifi开启成功
            //生成二维码并展示
            String content = packageSendInfo();
            image_qrcode.setImageBitmap(ScanUtils.encodeQRCode(content,
                    250, 250));
            text_tip.setText("文件：" + content);
        }else{
            Toast.makeText(SendShowActivity.this, "无线共享开启失败", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 封装二维码要传递的信息
     * @return
     */
    private String packageSendInfo(){

        List<BaseFileInfo> list = new ArrayList<>();
        //将缺失的信息补全
        for (int i = 0; i < baseFileInfoList.size(); i++){
            BaseFileInfo fileInfo = new BaseFileInfo();
            //如果传输的是app
            if (baseFileInfoList.get(i).getType() == BaseFileInfo.TYPE_APP){
                fileInfo.setName(baseFileInfoList.get(i).getName() + ".apk");
            }else{
                fileInfo.setName(baseFileInfoList.get(i).getName());
            }
            fileInfo.setPath(baseFileInfoList.get(i).getPath());
            fileInfo.setSize(new File(fileInfo.getPath()).length());
            fileInfo.setMd5(MD5Utils.getFileMD5(new File(fileInfo.getPath())));
            fileInfo.setType(baseFileInfoList.get(i).getType());
            list.add(fileInfo);
        }
        qrcodeInfo.setFiles(list);
        //将封装的信息转化为json字符串
        return JsonUtil.objToJson(qrcodeInfo);
    }

    private void startSend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开启socket连接
                try {
                    ServerSocket server = new ServerSocket(qrcodeInfo.getHostPort());
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
                        File[] files = new File[qrcodeInfo.getFiles().size()];
                        for (int i=0; i < files.length; i++){
                            File file = new File(qrcodeInfo.getFiles().get(i).getPath());
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
        }).start();

    }

    private void startAccept(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketInApp.startClientSocket("127.0.0.1", qrcodeInfo, null);
            }
        }).start();
    }
}
