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
 * filename=xxx;content-length=xxx;md5=xxx(定长的【@param HEADER_SIZE】个字节)
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

    //定义数据流信息前面header长度(字节)
    private static final int HEADER_SIZE = 512;

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
                //首先读取前面的HEADER_SIZE个字节
                System.out.println(c);
                byte[] b = new byte[HEADER_SIZE];
                in.read(b, 1, HEADER_SIZE-1);
                b[0] = (byte)c;
                System.out.println(Arrays.toString(b));
                //这里将空白的字节去掉
                byte[] availabByte = Utils.getAvailabByte(b);
                String strTmp = new String(availabByte, Charset.forName("UTF-8"));
                System.out.println("文件信息为：" + strTmp);
                String[] tmp = strTmp.split(";");
                //解析出文件名，长度和md5值
                String fileName = tmp[0].split("=")[1];
                long fileLength = Long.valueOf(tmp[1].split("=")[1]);
                String md5 = tmp[2].split("=")[1];
                //开始接收回调
                callback.start(fileName, fileLength, true);
                //根据文件长度继续读取文件实体信息
                FileOutputStream fos = new FileOutputStream(rootDir+fileName);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                long readLength = 0;
                //循环读取完所有的文件长度
                while (readLength < fileLength){
                    //如果剩下的长度在缓存区之内，则一次性读完
                    if (fileLength - readLength <= BUFFER_SIZE){
                        count = in.read(buffer, 0, (int)(fileLength-readLength));
                        System.out.println("文件结束内容："+Arrays.toString(buffer));
                    }else{
                        //表示剩下的长度在缓存区大小之外，则正常读取
                        count = in.read(buffer);
                    }
                    if (readLength <= BUFFER_SIZE){
                        //读取开始的内容
                        System.out.println("文件开始内容：" + Arrays.toString(buffer));
                    }
                    fos.write(buffer, 0, count);
                    readLength += count;
                    //处理进度值
                    callback.handlerProgress(fileName, fileLength, readLength, true);
                }
                fos.flush();
                fos.close();
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
                //首先写入文件相关信息
                //开始回调
                callback.start(file.getName(), file.length(), false);
                String md5 = Utils.getFileMD5(file);
                String tmp = "filename="+file.getName()
                        + ";content-length=" + file.length()
                        + ";md5=" + md5;
                System.out.println("写入文件信息：" + tmp);
                //首先写入文件的基础信息
                byte[] b = tmp.getBytes(Charset.forName("UTF-8"));
                System.out.println(Arrays.toString(b));
                out.write(b);
                //然后补齐至HEADER_SIZE个字节
                byte[] blank = new byte[HEADER_SIZE-b.length];
                System.out.println(Arrays.toString(blank));
                out.write(blank);
                //写入文件的实体
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                //定义变量保存已经传输的长度
                long transLength = 0;
                while ((count = fis.read(buffer)) != -1){
                    if (transLength == 0){
                        System.out.println("开始内容：" + Arrays.toString(buffer));
                    }

                    out.write(buffer, 0, count);
                    //将进度值通过回调返回
                    transLength += count;
                    callback.handlerProgress(file.getName(), file.length(), transLength, false);
                }
                System.out.println("结束内容：" + Arrays.toString(buffer));
                //关闭文件输入流
                fis.close();
                System.out.println("文件写入结束");
                //结束回调
                callback.end(file.getName(), false);
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
