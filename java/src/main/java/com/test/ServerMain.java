package com.test;

import com.yanling.socket.HttpSocketHandler;
import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;
import com.yanling.socket.SocketManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

/**
 * 服务端主程序
 * @author yanling
 * @date 2016-12-21
 */
public class ServerMain {

    public static void main(String[] args){

        //测试socket实现的http通信
        testHttpSocket();
        //testReadResources();
        //testHttp();
    }

    public static void testHttpSocket(){
        try {
            ServerSocket server = new ServerSocket(9000);
            while (!server.isClosed()){
                //获取连接的socket
                Socket socket = server.accept();
                HttpSocketHandler handler = new HttpSocketHandler(
                        "Server-Http", socket, cb
                );
                //设置文件传输列表
                handler.setFiles(new File("/home/yanling/桌面/test").listFiles());
                handler.rootDir = "/home/yanling/桌面/haha";
                SocketManager.getInstance().start(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testReadResources(){
        /*URL fileUrl = ServerMain.class.getResource("/upload.html");
        System.out.println(fileUrl.getFile());*/
        try{
            InputStream is = ServerMain.class.getResourceAsStream("/upload.html");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String tmp = "";
            while ((tmp = br.readLine()) != null){
                System.out.println(tmp);
            }
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    public static void testHttp(){
        try {
            ServerSocket server = new ServerSocket(9000);
            while (!server.isClosed()){
                Socket socket = server.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String tmp = "";
                while ((tmp = br.readLine()) != null){
                    System.out.println(tmp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static SocketCallback cb = new SocketCallback() {
        @Override
        public void start(String name, long totalSize, boolean isIn) {
            System.out.println("开始下载：" + name);
        }

        @Override
        public void handlerProgress(String name, long totalSize, long transSize, boolean isIn) {
            System.out.println("进度值为：" + transSize*100/totalSize);
        }

        @Override
        public void end(String name, boolean isIn) {
            System.out.println("结束下载");
        }

        @Override
        public void error(Exception e) {

        }
    };
}
