package com.yanling.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 基类的服务端socket
 * @author yanling
 * @date 2016-11-21
 */
public class BaseServerSocket {

    //定义可连接的客户端的最大数目
    private static final int MAX_CLIENT_COUNT = 10;

    //定义socket服务端口号
    public int ip_port;

    //定义变量标志当前连接客户端的数目
    public int count = 0;

    //定义服务端socket
    public ServerSocket serverSocket = null;


    public BaseServerSocket(){

    }

    public BaseServerSocket(int ip_port){
        this.ip_port = ip_port;
    }

    public void createServerSocket() throws IOException{
        serverSocket = new ServerSocket(ip_port);
        while (count++ < MAX_CLIENT_COUNT && !serverSocket.isClosed()){
            //接收客户端的响应
            Socket socket = serverSocket.accept();
            /*Thread t = new Thread(new BaseSocketHandler("tag", socket));
            t.start();*/
        }
    }

    /**
     * 关闭serversocket服务端
     */
    public void close(){
        if (serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
