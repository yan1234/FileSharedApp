package com.yanling.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 客户端和服务端都是Socket的处理（即Android客户端之间的通信）
 * 定义数据格式为：
 * header(文件的基本报头信息(定长的@param HEADER_SIZE))
 * body(文件的实体内容)
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

    //定义定长字节的占位符
    private static final char PLACEHOLDER = '$';

    //定义数据流信息前面header长度(字节)
    //这里其实300个字节就够了（因为文件名最长为255个字节），但是为了是2的倍数就采用了512
    private static final int HEADER_SIZE = 512;

    //定义字符信息的编码格式
    private static Charset charset = Charset.forName("UTF-8");

    //定义标志变量表示是否进行输入输出
    private int flag;

    //定义要传输的文件列表
    private File[] files;


    public SimpleSocketHandler(String tag, Socket socket, SocketCallback cb, int flag) throws IOException{
        super(tag, socket, cb);
        this.flag = flag;
    }

    @Override
    public void handlerInput() throws IOException{
        //根据标志处理输入流
        if (flag == FLAG_HANDLER_IN || flag == FLAG_HANDLER_INOUT){
            //read()默认会读取一个字节
            int c = 0;
            while ((c = in.read()) != -1){
                //解析数据流的头部信息
                String[] tmp = readHeader(c).split(";");
                //解析出文件名，长度和md5值
                String fileName = tmp[0].split("=")[1];
                long fileLength = Long.valueOf(tmp[1].split("=")[1]);
                String md5 = tmp[2].split("=")[1];
                //开始接收回调
                callback.start(fileName, fileLength, true);
                //读取文件实体信息
                readBody(fileName, fileLength);
                //文件下载结束
                callback.end(fileName, true);
            }
        }
    }

    @Override
    public void handlerOutput() throws IOException{
        //根据标志处理输出流
        if (flag == FLAG_HANDLER_OUT || flag == FLAG_HANDLER_INOUT){
            for (File file : files){
                //开始回调
                callback.start(file.getName(), file.length(), false);
                //写入头部信息
                writeHeader(file);
                //写入body信息
                writeBody(file);
                //结束回调
                callback.end(file.getName(), false);
            }
            out.flush();
            out.close();
        }
    }

    /**
     * 写入文件头部信息（主要分为两个部分）
     * 1、前面@param HEADER_SIZE 个字节的头部信息长度
     * 2、具体的相关文件数据信息（filename=xxx;content-length=xxx;md5=xxx）
     * @param file
     * @throws IOException
     */
    private void writeHeader(File file) throws IOException{
        String md5 = Utils.getFileMD5(file);
        String tmp = "filename="+file.getName()
                + ";content-length=" + file.length()
                + ";md5=" + md5;
        System.out.println("写入文件信息：" + tmp);
        //写入报头信息
        byte[] header = tmp.getBytes(charset);
        //文件的报头信息
        out.write(header);
        //写入占位符（即补齐4位）
        for (int i=header.length; i<HEADER_SIZE; i++){
            out.write((byte)PLACEHOLDER);
        }

    }


    /**
     * 读取头部信息
     * @param c，前面判断结束符读取的第一个字节
     * @return，返回读取到的报头信息
     * @throws IOException
     */
    private String readHeader(int c) throws IOException{
        //首先读取前面的HEADER_SIZE个字节,获取文件字符信息的长度
        byte[] header = new byte[HEADER_SIZE];
        header[0] = (byte)c;
        for (int i=1; i < HEADER_SIZE; i++){
            header[i] = (byte)in.read();
        }
        //这里将空白的字节去掉
        byte[] availabByte = Utils.getAvailabByte(header, PLACEHOLDER);
        String strTmp = new String(availabByte, charset);
        System.out.println("文件信息为：" + strTmp);
        return strTmp;
    }

    /**
     * 写入文件实体信息
     * @param file
     * @throws IOException
     */
    private void writeBody(File file) throws IOException{
        //写入文件的实体
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = 0;
        //定义变量保存已经传输的长度
        long transLength = 0;
        while ((count = fis.read(buffer)) != -1){
            out.write(buffer, 0, count);
            //将进度值通过回调返回
            transLength += count;
            callback.handlerProgress(file.getName(), file.length(), transLength, false);
        }
        //关闭文件输入流
        fis.close();
    }

    /**
     * 读取文件实体信息并写入到文件
     * @param fileName，文件名
     * @param fileLength，文件的长度
     * @throws IOException
     */
    private void readBody(String fileName, long fileLength) throws IOException{
        //根据文件长度继续读取文件实体信息
        File targetDir = new File(rootDir);
        if (!targetDir.exists()){
            targetDir.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(rootDir + fileName);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = 0;
        long readLength = 0;
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
            readLength += count;
            //处理进度值
            callback.handlerProgress(fileName, fileLength, readLength, true);
        }
        fos.flush();
        fos.close();
    }



    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }
}
