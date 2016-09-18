package com.example.filesharedapp.app.transfers;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.transfers.entity.FileInfo;
import com.example.filesharedapp.app.transfers.entity.QrcodeInfo;
import com.example.filesharedapp.framework.ui.base.BaseActivity;
import com.example.filesharedapp.utils.json.JsonUtil;
import com.example.filesharedapp.utils.md5.MD5Utils;
import com.example.scanlibrary.ScanUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SendShowActivity extends BaseActivity {

    //二维码图片
    private ImageView image_qrcode;
    //提示文字
    private TextView text_tip;

    //定义待发送的条码信息对象
    private QrcodeInfo qrcodeInfo = null;
    //定义待发送的文件列表对象
    private List<FileInfo> fileInfoList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_show);
        //获取上一个界面传递过来的待发送的文件列表
        //fileInfoList = (List<FileInfo>)getIntent().getExtras().getSerializable("fileinfos");
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

        fileInfoList = new ArrayList<>();
        FileInfo  fileInfo = new FileInfo();
        File file  = new File(Environment.getExternalStorageDirectory() + File.separator + "test.apk");
        fileInfo.setFileName(file.getName());
        fileInfo.setPath(file.getPath());
        fileInfo.setType(1);   //app
        fileInfo.setSize(file.length());
        fileInfo.setMd5(MD5Utils.getFileMD5(file));
        fileInfoList.add(fileInfo);


        qrcodeInfo = new QrcodeInfo();
        //随机生成端口号(50000-59999之间)
        qrcodeInfo.setHostPort(50000 + new Random().nextInt(10000));
        qrcodeInfo.setFiles(fileInfoList);
        //将封装的信息转化为json字符串
        return JsonUtil.objToJson(qrcodeInfo);
    }

    private void startSend(){
        Log.d("haha", "端口号为：" + qrcodeInfo.getHostPort());
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ServerSocket server = new ServerSocket(qrcodeInfo.getHostPort());
                    Socket socket = server.accept();

                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();

                    OutputStreamWriter osw = new OutputStreamWriter(out, "utf-8");
                    //获取文件的输入流
                    FileInputStream fis = new FileInputStream(new File(qrcodeInfo.getFiles().get(0).getPath()));

                    int length = 0;
                    byte[] buff = new byte[1024];
                    while((length = fis.read(buff)) != -1){
                        out.write(buff, 0, length);
                    }

                    out.flush();
                    out.close();
                    fis.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }
}
