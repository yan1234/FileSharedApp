package com.example.filesharedapp.core;

import com.example.filesharedapp.framework.media.entity.BaseFileInfo;
import com.example.filesharedapp.app.transfers.entity.QrcodeInfo;
import com.example.filesharedapp.framework.storage.StorageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * App之间实现socket通信（主要用于app间文件传输）
 * @author yanling
 * @date 2016-09-18
 */
public class SocketInApp {


    /**
     * 开启socket 服务端
     * @param qrcodeInfo，传递的文件信息
     */
    public static void startServerSocket(final QrcodeInfo qrcodeInfo){

        try {
            //初始化socket服务端
            ServerSocket serverSocket = new ServerSocket(qrcodeInfo.getHostPort());
            //获取连接socket客户端
            Socket socket = serverSocket.accept();
            //定义输入输出流
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            /**
             * 向客户端写入文件数据流
             * 写入格式为先写入32位的文件MD5值然后再写入数据
             */
            for (BaseFileInfo baseFileInfo : qrcodeInfo.getFiles()){
                out.write(baseFileInfo.getMd5().getBytes(Charset.forName("utf-8")));
                FileInputStream fis = new FileInputStream(new File(baseFileInfo.getPath()));
                int length = 0;
                byte[] buffer = new byte[1024];
                //读取文件流
                while ((length = fis.read(buffer)) != -1){
                    out.write(buffer, 0, length);
                }
                //关闭文件输出流
                fis.close();
            }
            //关闭socket输出流
            out.flush();
            out.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 开启客户端socket
     * @param ip，服务端的ip地址
     * @param qrcodeInfo，待接收的信息
     */
    public static void startClientSocket(final String ip, final QrcodeInfo qrcodeInfo){
        try {
            //将文件列表存储在hashmap中便于查找
            HashMap<String, BaseFileInfo> map = new HashMap<String, BaseFileInfo>();
            for (BaseFileInfo baseFileInfo : qrcodeInfo.getFiles()){
                map.put(baseFileInfo.getMd5(), baseFileInfo);
            }
            Socket socket = new Socket(ip, qrcodeInfo.getHostPort());
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            //定义已下载文件数量
            int filecount = 0;
            while (filecount <= qrcodeInfo.getFiles().size()){
                //没有读取就接着读
                //首先读取32个字节数据
                byte[] buffer = new byte[1024];
                in.read(buffer, 0, 32);
                //获取读到的md5值
                String md5 = new String(buffer);
                BaseFileInfo baseFileInfo = map.get(md5);
                //将数据流输出到文件
                FileOutputStream fos = new FileOutputStream(
                        StorageManager.getInstance().getDownload() + File.separator + baseFileInfo.getName());
                //定义下载的长度
                int downloadSize = 0;
                int length = 0;
                //遍历文件长度下载
                while (downloadSize <= baseFileInfo.getSize()){
                    //判断未传输的长度是否超过了buffer缓冲区长度
                    if (baseFileInfo.getSize() - downloadSize <= 1024){
                        //直接读取剩余的长度
                        length = in.read(buffer, 0, (int)(baseFileInfo.getSize() - downloadSize));
                        fos.write(buffer, 0, length);
                    }else{
                        //按照默认的缓冲区大小读取
                        length = in.read(buffer);
                        fos.write(buffer, 0, length);
                    }
                }
                //读取完毕后关闭文件输入流
                fos.flush();
                fos.close();
            }
            //所有的文件读取完毕则关闭输入流
            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
