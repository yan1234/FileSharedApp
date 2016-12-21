package com.yanling.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 用来处理客户端socket响应的线程
 */
public abstract class BaseSocketHandler implements Runnable{

    //定义处理标记
    private String tag;
    //定义socket套接字
    private Socket socket = null;

    //定义输入输出流
    public InputStream in;
    public OutputStream out;

    public BaseSocketHandler(String tag, Socket socket) throws IOException{
        this.tag = tag;
        this.socket = socket;
        //得到输入输出流
        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
    }

    @Override
    public void run(){
        try{
            //处理输入数据
            handlerInput();
            //处理输出数据
            handlerOutput();
        }catch (IOException e){
            e.printStackTrace();
        }

        //关闭
        close();
    }

    /**
     * 关闭socket连接
     */
    public void close(){
        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理输入数据
     */
    public abstract void handlerInput() throws IOException;

    /**
     * 处理输出数据
     */
    public abstract void handlerOutput() throws IOException;

}