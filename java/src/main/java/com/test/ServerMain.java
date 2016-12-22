package com.test;

import com.yanling.socket.HttpSocketHandler;
import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端主程序
 * @author yanling
 * @date 2016-12-21
 */
public class ServerMain {

    public static void main(String[] args){
        try {
            //普通socket服务端测试
            /*ServerSocket server = new ServerSocket(9999);
            Socket socket = server.accept();

            SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler(
                    "ServerMain", socket, SimpleSocketHandler.FLAG_HANDLER_OUT
            );
            //设置传输的文件列表
            File file = new File("C:\\zzz");
            simpleSocketHandler.setFiles(file.listFiles());
            //开启线程
            new Thread(simpleSocketHandler).start();*/

            //socket服务端实现http协议测试
            ServerSocket server = new ServerSocket(9999);
            Socket socket = server.accept();
            HttpSocketHandler httpSocketHandler = new HttpSocketHandler(
                    "ServerHttpMain", socket, cb
            );
            new Thread(httpSocketHandler).start();

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
