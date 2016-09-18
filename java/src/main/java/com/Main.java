package com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by 272388 on 2016/9/13.
 */
public class Main {

    public static void main(String[] args){

        File file = new File("E:\\tempUpload\\test.apk");


        //初始化socket客户端
        try {
            Socket socket = new Socket("10.226.174.12", 50545);

            InputStream in = socket.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = in.read(buff)) != -1){
                fos.write(buff, 0, length);
            }
            fos.flush();
            fos.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
