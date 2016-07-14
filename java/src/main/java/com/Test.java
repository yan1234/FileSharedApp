package com;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 测试类
 */
public class Test {


    public static void main(String[] args){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8888);
            while (true){
                Socket socket = serverSocket.accept();
                InputStream in = socket.getInputStream();
                PrintStream printStream = new PrintStream(socket.getOutputStream());

                //readInputStream(in);

                //封装响应头
                StringBuilder sb = new StringBuilder();
                //sb.append("Server: yanling").append("\r\n");
                //sb.append("Content-Encoding: UTF-8").append("\r\n");
                //sb.append("Content-Type: text/html;charset=utf-8").append("\r\n");
                //获取内容
                //String content = getFileContent("E:\\Upload.html");
                File file = new File("E:\\aapt.exe");
                FileInputStream fis = new FileInputStream(file);
                int length = 0;
                byte[] buffer = new byte[1024];
                while ((length = fis.read(buffer)) != -1){
                    printStream.write(buffer, 0, length);
                }
                //sb.append("Content-Length: "+content.length()).append("\r\n");
                //sb.append("\r\n");
                //printStream.write(sb.toString().getBytes());
                printStream.flush();
                printStream.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void readInputStream(InputStream in){
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        int length = 0;
        //读取一定数据到缓冲区
        try{
            String line = "";
            while ((line = br.readLine()) != null){
                int i = 0;
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    private static String getFileContent(String filePath){
        File file = new File(filePath);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String temp = "";
            while ((temp = br.readLine()) != null){
                sb.append(temp);
                sb.append("\r\n");
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }



}
