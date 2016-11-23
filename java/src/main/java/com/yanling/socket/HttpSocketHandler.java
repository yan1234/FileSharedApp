package com.yanling.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Socket实现http协议，并充当web服务器
 * @author yanling
 * @date 2016-11-21
 */
public class HttpSocketHandler extends BaseSocketHandler{

    //用户做下载的默认html页面
    private static final String DOWNLOAD = "download";
    //用于上传文件的html页面
    private static final String UPLOAD_PAGE = "upload.html";
    //用于做上传处理的action
    private static final String UPLOAD_ACTION = "fileUpload";
    //用于展示错误的html页面
    private static final String ERROR_PAGE = "error.html";

    //定义缓冲区大小
    private static final int BUFFER_SIZE = 1024 * 10;

    //定义变量保存当前请求待处理的操作，主要分为上面三种：下载、页面展示、上传。
    private String outputPage;

    //定义上传文件名
    private String fileName;
    //定义上传文件的长度
    private long fileSize;
    //定义http上传的body的长度
    private long content_length;


    public HttpSocketHandler(String tag, Socket socket) throws IOException {
        super(tag, socket);
    }

    @Override
    public void handlerInput() throws IOException{
        //解析http协议
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.forName("ISO-8859-1")));
        //解析http协议的startline(即第一行内容)
        String firstLine = bufferedReader.readLine();
        //如果解析出来是下载操作，则跳转到下载处理
        if (parseRequestStartLine(firstLine)){
            return;
        }
        //保存每一行解析的内容
        String line = "";
        //定义除文件外，其他数据的长度
        long body_other_count = 0;
        String boundary = "";
        //读取到这里基本就可以断定是文件上传操作了
        while((line = bufferedReader.readLine()) != null){
            //解析http上传数据中body的长度
            //Content-Length: 6521899
            if (line.toLowerCase().contains("Content-Length".toLowerCase())){
                String[] tmp = line.split(":");
                content_length = Long.parseLong(tmp[1].replace(" ", ""));
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
                body_other_count += line.length() + "\r\n".length();
                //确定好起始行后下面就是文件相关信息数据了，包括文件名，数据类型等
                break;
            }
        }
        //解析上传流文件的文件名
        //Content-Disposition: form-data; name="upload"; filename="apktool.jar"
        line = bufferedReader.readLine();
        String[] tmp = line.split(";")[2].split("=");
        //去掉双引号
        fileName = tmp[1].replace("\"", "");
        //记录长度
        body_other_count += line.length() + "\r\n".length();
        //记录长度Content-Type: application/octet-stream
        body_other_count += bufferedReader.readLine().length() + "\r\n".length();
        //记录长度\r\n
        body_other_count += bufferedReader.readLine().length() + "\r\n".length();
        //剔除结束符
        body_other_count += "\r\n".length() + "--".length() * 2 + boundary.length() + "\r\n".length();
        //获取上传文件的实际长度
        fileSize = content_length -  body_other_count;
        //下面开始就是完整的文件数据了
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        char[] buff = new char[BUFFER_SIZE];
        int count = 0;
        //定义变量保存已读的文件数据长度
        //total_count最后的长度应该是和upload_length长度一致
        long total_count = 0;
        while ((count = bufferedReader.read(buff)) != -1){
            Charset cs = Charset.forName("ISO-8859-1");
            CharBuffer cb = CharBuffer.allocate(buff.length);
            cb.put(buff);
            cb.flip();
            ByteBuffer bb = cs.encode(cb);
            //避免写入数据过多
            if (fileSize - total_count >= BUFFER_SIZE){
                //如果当前读取的数量与总数的差距在缓存区数据长度以上，正常读取就可以了
                fos.write(bb.array(), 0, count);
                total_count += count;
            }else{
                //如果差距在缓冲区长度以内
                if (count > fileSize - total_count){
                    //如果剩下的数据包含在当前数据中，直接把剩下读完就ok了
                    fos.write(bb.array(), 0, (int)(fileSize - total_count));
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

    }

    @Override
    public void handlerOutput() throws IOException{
        //展示输出对应的页面
        download(outputPage);
        /*//下载操作
        if (DOWNLOAD.equals(operate)){
            download("filePath");
        }//上传页面展示操作
        else if (UPLOAD_PAGE.equals(operate)){
            //这里展示上传页面就是向客户端输入下载页面文件
            download("uploadPage");
        }//上传操作
        else if (UPLOAD_ACTION.equals(operate)){
            //上传成功后展示成功界面
            download("success");
        }//错误页面
        else if (ERROR_PAGE.equals(operate)){
            download("error");
        }*/
    }

    /**
     * 下载文件
     * @param filePath，待下载的文件路径
     * @throws IOException
     */
    private void download(String filePath) throws IOException{
        try {
            //得到上传页面文件
            FileInputStream fis = new FileInputStream(new File(filePath));
            byte[] buffer = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count=fis.read(buffer)) != -1){
                out.write(buffer, 0, count);
            }
            //关闭输入输出流
            fis.close();
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 解析Http请求的StartLine，用于做http路由转发（定位url请求地址）
     * @param line，startline的内容
     * @return true：表示输入流解析结束，直接返回，false：表示继续解析余下数据
     */
    private boolean parseRequestStartLine(String line){
        //表示下载操作
        if (DOWNLOAD.equals(line)){
            outputPage = "downloadFilePath";
            return true;
        }//表示上传页面请求
        else if (UPLOAD_PAGE.equals(line)){
            outputPage = "uploadPageFileName";
            return true;
        }//表示上传数据操作请求
        else if (UPLOAD_ACTION.equals(line)){
            outputPage = "successPageFileName";
            return false;
        }else{
            //展示错误提示的页面
            outputPage = "errorPageFileName";
            return true;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getContent_length() {
        return content_length;
    }

    public void setContent_length(long content_length) {
        this.content_length = content_length;
    }
}
