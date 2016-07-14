package com.yanling.http;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 基类的ServerSocket服务端类
 * @author yanling
 * @date 2016-07-14 11:34
 */
public abstract class BaseServerSocket {

    //定义端口号
    private int address_port;

    //定义服务端Socket对象
    private ServerSocket server;


    /**
     * 通过端口号开启服务端监听
     * @param port
     * @throws IOException
     */
    public BaseServerSocket(int port) throws IOException{
        this.address_port = port;
        server = new ServerSocket(port);
    }


    public int getAddress_port() {
        return address_port;
    }

    public void setAddress_port(int address_port) {
        this.address_port = address_port;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }
}
