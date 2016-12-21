package com.test;

import com.yanling.socket.SimpleSocketHandler;

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
            ServerSocket server = new ServerSocket(9999);
            Socket socket = server.accept();

            SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler(
                    "ServerMain", socket, SimpleSocketHandler.FLAG_HANDLER_OUT
            );
            //设置传输的文件列表
            File file = new File("C:\\zzz");
            simpleSocketHandler.setFiles(file.listFiles());
            //开启线程
            new Thread(simpleSocketHandler).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
