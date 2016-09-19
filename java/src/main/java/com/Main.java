package com;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;


/**
 * Created by 272388 on 2016/9/13.
 */
public class Main {

    public static void main(String[] args){

        String a = "38B122C2C849FF6D92BE3408502090E7";
        byte[] buffer = a.getBytes(Charset.forName("utf-8"));
        String b = new String(buffer);
        System.out.println(buffer.length);
        System.out.println(b);

        /*try {
            Socket socket = new Socket("10.226.172.90", 53221);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            byte[] buffer = new byte[1024];
            in.read(buffer, 0, 32);
            String md5 = buffer.toString();
            System.out.println(md5);

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        while (true){

        }



    }
}
