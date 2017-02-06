package com.yanling.fileshared.app.transfers.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanling.fileshared.R;
import com.yanling.fileshared.framework.Constants;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;
import com.yanling.fileshared.app.transfers.android.entity.QrcodeInfo;
import com.yanling.fileshared.framework.ui.base.BaseActivity;
import com.yanling.fileshared.framework.wifi.WifiController;
import com.yanling.fileshared.utils.json.JsonUtil;
import com.yanling.fileshared.utils.md5.MD5Utils;
import com.yanling.android.scanlibrary.ScanUtils;
import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SendBetweenAppActivity extends BaseActivity {



    //二维码图片
    private ImageView image_qrcode;
    //提示文字
    private TextView text_tip;
    //定义待发送的条码信息对象
    private QrcodeInfo qrcodeInfo = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_show);
        //获取传递过来的条码信息
        qrcodeInfo = (QrcodeInfo)getIntent().getExtras().getSerializable(Constants.BUNDLE_KEY_QRCODEINFO);
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
        String content = packageSendInfo();
        image_qrcode.setImageBitmap(ScanUtils.encodeQRCode(content,
                250, 250));
        text_tip.setText("文件：" + content);
    }


    /**
     * 封装二维码要传递的信息
     * @return
     */
    private String packageSendInfo(){

        List<BaseFileInfo> list = new ArrayList<>();
        //将缺失的信息补全
        for (int i = 0; i < qrcodeInfo.getFiles().size(); i++){
            BaseFileInfo fileInfo = new BaseFileInfo();
            //如果传输的是app
            if (qrcodeInfo.getFiles().get(i).getType() == BaseFileInfo.TYPE_APP){
                fileInfo.setName(qrcodeInfo.getFiles().get(i).getName() + ".apk");
            }else{
                fileInfo.setName(qrcodeInfo.getFiles().get(i).getName());
            }
            fileInfo.setPath(qrcodeInfo.getFiles().get(i).getPath());
            fileInfo.setSize(new File(fileInfo.getPath()).length());
            fileInfo.setMd5(MD5Utils.getFileMD5(new File(fileInfo.getPath())));
            fileInfo.setType(qrcodeInfo.getFiles().get(i).getType());
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
    }
}
