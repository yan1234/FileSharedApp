package com.test;

import com.yanling.socket.SimpleSocketHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * 客户端主程序
 * @author yanling
 * @date 2016-12-21
 */
public class ClientMain {

    public static void main(String[] args){
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler(
                "ClientMain", socket, SimpleSocketHandler.FLAG_HANDLER_IN
            );
            new Thread(simpleSocketHandler).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
