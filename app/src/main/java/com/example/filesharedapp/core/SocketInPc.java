package com.example.filesharedapp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * 通过socket实现http协议，在手机端开启http服务器
 */
public class SocketInPc {

    //用户做下载的默认html页面
    private static final String INDEX = "index.html";
    //用于上传文件的html页面
    private static final String UPLOAD_PAGE = "upload.html";
    //用于做上传处理的action
    private static final String UPLOAD_ACTION = "fileUpload";


    //保存上传数据的字节流长度
    private long upload_length;
    //保存上传的文件名
    private String upload_filename;
    //保存http协议中的boundary
    private String boundary;


    public void startUpload(int port){
        try {
            //开启socket服务端
            ServerSocket serverSocket = new ServerSocket(port);
            //等待客户端连接
            Socket socket = serverSocket.accept();
            //定义输入输出流
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("ISO-8859-1")));
            //定义临时变量保存每一行的内容
            String line = null;
            //定义变量保存当前解析的行数
            int length = 0;
            //定义变量记录请求body实体中其他数据的长度
            int body_count = 0;
            //解析流中的内容
            /*byte[] b = new byte[1];
            //变量保存当前读取流的长度
            int count = 0;*/
                while ((line = br.readLine()) != null){
                    ++length;
                    ////解析http请求的startLine
                    if (length == 1){
                        //判断解析的结果
                        if (parseStartLine(line)){
                            //只要不是上传操作，直接就结束
                            return;
                        }
                    }
                    //第一行解析完后基本可以确定后面的是文件上传操作了
                    //解析上传流的长度，这里的长度指的是整个body的长度，包括实际内容和http的一些标志信息
                    //Content-Length: 6521899
                    if (line.toLowerCase().contains("Content-Length".toLowerCase())){
                        String[] tmp = line.split(":");
                        upload_length = Long.parseLong(tmp[1].replace(" ", ""));
                        continue;
                    }
                    //解析出boundary
                    //Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryOml2B50fQJCENuAl
                    if (line.toLowerCase().contains("Content-Type".toLowerCase())
                            && line.toLowerCase().contains("boundary".toLowerCase())){
                        String[] tmp = line.split(";")[1].split("=");
                        boundary = tmp[1];
                        continue;
                    }

                    //根据boundary确定实体的起始行
                    if (boundary != null && !"".equals(boundary) && line.contains(boundary)){
                        //注意换行符"\r\n"
                        body_count += line.length() + "\r\n".length();
                        //确定好起始行后下面就是文件相关信息数据了，包括文件名，数据类型等
                        break;
                    }
                }
                //解析上传流文件的文件名
                //Content-Disposition: form-data; name="upload"; filename="apktool.jar"
                line = br.readLine();
                String[] tmp = line.split(";")[2].split("=");
                //去掉双引号
                upload_filename = tmp[1].replace("\"", "");
                //记录长度
                body_count += line.length() + "\r\n".length();
                //记录长度Content-Type: application/octet-stream
                body_count += br.readLine().length() + "\r\n".length();
                //记录长度\r\n
                body_count += br.readLine().length() + "\r\n".length();
                //剔除结束符
                body_count += "\r\n".length() + "--".length() * 2 + boundary.length() + "\r\n".length();
                //获取上传文件的实际长度
                upload_length -= body_count;
                //下面开始就是完整的文件数据了
                File file = new File("E:\\" + upload_filename);
                FileOutputStream fos = new FileOutputStream(file);
                char[] buff = new char[1024];
                int count = 0;
                //定义变量保存已读的文件数据长度
                //total_count最后的长度应该是和upload_length长度一致
                long total_count = 0;
                while ((count = br.read(buff)) != -1){
                    Charset cs = Charset.forName("ISO-8859-1");
                    CharBuffer cb = CharBuffer.allocate(buff.length);
                    cb.put(buff);
                    cb.flip();
                    ByteBuffer bb = cs.encode(cb);
                    //避免写入数据过多
                    if (upload_length - total_count >= 1024){
                        //如果当前读取的数量与总数的差距在缓存区数据长度以上，正常读取就可以了
                        fos.write(bb.array(), 0, count);
                        total_count += count;
                    }else{
                        //如果差距在缓冲区长度以内
                        if (count > upload_length - total_count){
                            //如果剩下的数据包含在当前数据中，直接把剩下读完就ok了
                            fos.write(bb.array(), 0, (int)(upload_length - total_count));
                            //结束
                            break;
                        }else{
                            //剩下数据一次还不能读完，那么就再读一次
                            fos.write(bb.array(), 0, count);
                            total_count += count;
                        }
                    }

                }
                fos.flush();
                fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析http请求的startline，用于做http路由转发（定位url请求地址）
     * @param line，startline的内容
     * @return true：表示输入流解析结束，直接返回，false：表示继续解析余下数据
     */
    private static boolean parseStartLine(String line){
        //判断是否是默认下载页面
        if (line.contains(INDEX)){
            //不用展示页面，直接返回下载的文件流
            return true;
        }
        //判断是否是上传的html
        if (line.contains(UPLOAD_PAGE)){
            //返回upload.html页面数据
            return true;
        }
        //判断是否是上传文件
        if (line.contains(UPLOAD_ACTION)){
            //返回标志接着处理上传的数据
            return false;
        }
        return true;

    }

}
