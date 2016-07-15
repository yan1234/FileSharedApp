package com.yanling.http;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * 通过Socket实现web端的http请求响应
 * @author yanling
 * @date 2016-07-14 11:40
 */
public class WebServerSocket extends BaseServerSocket{


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


    public WebServerSocket(int port) throws IOException{
        super(port);
    }

    /**
     * 解析输入流（主要是从输入流中解析出http协议信息
     * @param in
     * @param callback
     */
    public void parseStream(InputStream in, WebServerCallback callback){

        BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("ISO-8859-1")));
        //定义临时变量保存每一行的内容
        String line = null;
        //定义变量保存当前解析的行数
        int length = 0;

        //解析流中的内容
        try{
            /*byte[] b = new byte[1];
            //变量保存当前读取流的长度
            int count = 0;*/
            while ((line = br.readLine()) != null){
                ++length;
                ////解析http请求的startLine
                if (length == 1){
                    //判断解析的结果
                    if (parseStartLine(line, callback)){
                        //只要不是上传操作，直接就结束
                        return;
                    }
                }
                //第一行解析完后基本可以确定后面的是文件上传操作了
                //解析上传流的长度
                //---Content-Length: 6521899---
                if (line.toLowerCase().contains("Content-Length".toLowerCase())){
                    String[] tmp = line.split(":");
                    upload_length = Long.parseLong(tmp[1].replace(" ", ""));
                }

                //解析上传流文件的文件名
                //---Content-Disposition: form-data; name="upload"; filename="apktool.jar"---
                if (line.toLowerCase().contains("Content-Disposition".toLowerCase())
                        && line.toLowerCase().contains("filename".toLowerCase())){
                    String[] tmp = line.split(";")[2].split("=");
                    //去掉双引号
                    upload_filename = tmp[1].replace("\"", "");
                    //解析到这里，那么下面再读两行就是文件流数据了
                    break;
                }
            }
            //去掉--Content-Type: application/octet-stream--
            String test1 = br.readLine();
            //去掉--\r\n
            String test2 = br.readLine();
            //下面开始就是完整的文件数据了
            File file = new File("E:\\" + upload_filename);
            FileOutputStream fos = new FileOutputStream(file);
            char[] buff = new char[1024];
            int count = 0;
            while ((count = br.read(buff)) != -1){
                Charset cs = Charset.forName("ISO-8859-1");
                CharBuffer cb = CharBuffer.allocate(buff.length);
                cb.put(buff);
                cb.flip();
                ByteBuffer bb = cs.encode(cb);
                fos.write(bb.array(), 0, count);
            }
            fos.flush();
            fos.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 解析http请求的startline，用于做http路由转发（定位url请求地址）
     * @param line，startline的内容
     * @param callback, 解析处理回调
     * @return true：表示输入流解析结束，直接返回，false：表示继续解析余下数据
     */
    private boolean parseStartLine(String line, WebServerCallback callback){
        //判断是否是默认下载页面
        if (line.contains(INDEX)){
            //不用展示页面，直接返回下载的文件流
            callback.handleDownload();
            return true;
        }
        //判断是否是上传的html
        if (line.contains(UPLOAD_PAGE)){
            //返回upload.html页面数据
            callback.showUploadPage();
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
