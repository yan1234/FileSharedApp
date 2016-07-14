package com.yanling.http;

import java.io.IOException;

/**
 * 普通的服务端ServerSocket
 * @author yanling
 * @date 2016-07-14 11:44
 */
public class SimpleServerSocket extends BaseServerSocket{

    public SimpleServerSocket(int port) throws IOException {
        super(port);
    }
}
