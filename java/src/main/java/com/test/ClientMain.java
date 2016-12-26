package com.test;

import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;

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
            Socket socket = new Socket("10.226.172.41", 50426);
            SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler(
                "ClientMain", socket, cb, SimpleSocketHandler.FLAG_HANDLER_IN
            );
            new Thread(simpleSocketHandler).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SocketCallback cb = new SocketCallback() {
        @Override
        public void start(String name, long totalSize, boolean isIn) {

        }

        @Override
        public void handlerProgress(String name, long totalSize, long transSize, boolean isIn) {

        }

        @Override
        public void end(String name, boolean isIn) {

        }

        @Override
        public void error(Exception e) {

        }
    };
}
