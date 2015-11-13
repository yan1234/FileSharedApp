package com.example.core.web;

import java.net.Socket;

/**
 * 利用ServerSocket实现web服务器端
 * Created by yanling on 2015/11/12.
 */
public class WebSocket {

    //定义Socket客户端
    private Socket client;

    public WebSocket(Socket socket){
        this.client = socket;
    }
    


}
