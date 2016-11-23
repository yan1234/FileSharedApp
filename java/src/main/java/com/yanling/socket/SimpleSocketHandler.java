package com.yanling.socket;

import java.io.IOException;
import java.net.Socket;

/**
 * 客户端和服务端都是Socket的处理（即Android客户端之间的通信）
 * TODO：后期考虑将Android客户端之间的socket通信也用http协议实现
 * @author yanling
 * @date 2016-11-21
 */
public class SimpleSocketHandler extends BaseSocketHandler{


    public SimpleSocketHandler(String tag, Socket socket) throws IOException{
        super(tag, socket);
    }

    @Override
    public void handlerInput() {

    }

    @Override
    public void handlerOutput() {

    }
}
