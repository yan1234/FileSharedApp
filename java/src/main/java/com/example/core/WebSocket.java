package com.example.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 用socket实现web服务
 * Created by yanling on 2015/11/12.
 */
public class WebSocket {

    //定义socket服务端
    private ServerSocket serverSocket;
    //定义socket客户端
    private Socket socketClient;
    //定义输入流
    private InputStream inputStream;
    //定义输出流
    private OutputStream outputStream;


    public static void main(String[] args){
        WebSocket webSocket = new WebSocket();
        try {
            //开启一个socket服务端
            webSocket.serverSocket = new ServerSocket(9000);
            while (true){
                //连接上服务端
                webSocket.socketClient = webSocket.serverSocket.accept();

                StringBuffer request = new StringBuffer(2018);

                int i;
                byte[] buffer = new byte[2048];

                try {
                    i = webSocket.socketClient.getInputStream().read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    i = -1;
                }

                for (int j = 0; j < i; j ++) {
                    request.append((char)buffer[j]);
                }

                System.out.println("-----------------request----------------");
                System.out.print(request.toString());

                String url = parseUri(request.toString());

                //输出浏览器端的响应头部信息
                writerHeader(webSocket.socketClient.getOutputStream());
                /*while ((count = webSocket.socketClient.getInputStream().read(data)) != -1){
                    System.out.println("长度为："+count);
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String parseUri(String requestString) {

        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            return requestString.substring(index1 + 1, index2);
        }
        return null;

    }
    private static void writerHeader(OutputStream out){
        File file = new File("E:\\aapt.exe");
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[1024];
            int count = 0;
            while ((count = fis.read(data)) != -1) {
                out.write(data,0, count);
            }
            out.flush();
            out.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
