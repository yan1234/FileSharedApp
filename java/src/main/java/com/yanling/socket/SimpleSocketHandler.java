package com.yanling.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * 客户端和服务端都是Socket的处理（即Android客户端之间的通信）
 * 定义数据格式为：
 * filename=xxx;content-length=xxx;md5=xxx(定长的1024个字节)
 * xxx...(内容)
 * TODO：后期考虑将Android客户端之间的socket通信也用http协议实现
 * @author yanling
 * @date 2016-11-21
 */
public class SimpleSocketHandler extends BaseSocketHandler{


    //定义标志变量表示是否进行输入输出
    //0: 只输入不输出；1:不输入只输出；2：既输入又输出
    public static final int FLAG_HANDLER_IN = 0;
    public static final int FLAG_HANDLER_OUT = 1;
    public static final int FLAG_HANDLER_INOUT = 2;

    public static String rootDir = "C:\\";

    //定义缓冲区大小
    private static final int BUFFER_SIZE = 5 * 1024;

    //定义标志变量表示是否进行输入输出
    private int flag;

    //定义要传输的文件列表
    private File[] files;

    public SimpleSocketHandler(String tag, Socket socket) throws IOException{
        super(tag, socket);
    }

    public SimpleSocketHandler(String tag, Socket socket, int flag) throws IOException{
        super(tag, socket);
        this.flag = flag;
    }

    @Override
    public void handlerInput() throws IOException{
        //根据标志处理输入流
        if (flag == FLAG_HANDLER_IN || flag == FLAG_HANDLER_INOUT){
            //read()默认会读取一个字节
            int c = 0;
            while ((c = in.read()) != -1){
                //首先读取前面的1024个字节
                byte[] b = new byte[1024];
                in.read(b, 1, 1023);
                b[0] = (byte)c;
                //这里将空白的字节去掉
                byte[] availabByte = Utils.getAvailabByte(b);
                String strTmp = new String(availabByte, Charset.forName("UTF-8"));
                String[] tmp = strTmp.split(";");
                //解析出文件名，长度和md5值
                String fileName = tmp[0].split("=")[1];
                Long fileLength = Long.valueOf(tmp[1].split("=")[1]);
                String md5 = tmp[2].split("=")[1];
                //根据文件长度继续读取文件实体信息
                FileOutputStream fos = new FileOutputStream(rootDir+fileName);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                int readLength = 0;
                //循环读取完所有的文件长度
                while (readLength < fileLength){
                    //如果剩下的长度在缓存区之内，则一次性读完
                    if (fileLength - readLength <= BUFFER_SIZE){
                        count = in.read(buffer, 0, (int)(fileLength-readLength));
                    }else{
                        //表示剩下的长度在缓存区大小之外，则正常读取
                        count = in.read(buffer);
                    }
                    fos.write(buffer, 0, count);
                    fos.flush();
                    fos.close();
                    readLength += count;
                }
            }
        }
    }

    @Override
    public void handlerOutput() throws IOException{
        //根据标志处理输出流
        if (flag == FLAG_HANDLER_OUT || flag == FLAG_HANDLER_INOUT){
            for (File file : files){
                //首先写入文件相关信息
                String md5 = Utils.getFileMD5(file);
                String tmp = "filename="+file.getName()
                        + ";content-length=" + file.length()
                        + ";md5=" + md5;
                //首先写入文件的基础信息
                byte[] b = tmp.getBytes(Charset.forName("UTF-8"));
                out.write(b);
                //然后补齐至1024个字节
                byte[] blank = new byte[1024-b.length];
                out.write(blank);
                //写入文件的实体
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = fis.read(buffer)) != -1){
                    out.write(buffer, 0, count);
                }
                //关闭文件输入流
                fis.close();
            }
            out.flush();
            out.close();
        }
    }



    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }
}
