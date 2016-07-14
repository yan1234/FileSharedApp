package com.yanling.http;

import java.io.IOException;

/**
 * 通过Socket实现web端的http请求响应
 * @author yanling
 * @date 2016-07-14 11:40
 */
public class WebServerSocket extends BaseServerSocket{

    public WebServerSocket(int port) throws IOException{
        super(port);
    }
}
