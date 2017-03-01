package com.yanling.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Socket实现http协议，并充当web服务器
 * @author yanling
 * @date 2016-11-21
 */
public class HttpSocketHandler extends BaseSocketHandler{

    //用户做下载的默认html页面
    private static final String DOWNLOAD_PAGE = "/download.html";
    //用于上传文件的html页面
    private static final String UPLOAD_PAGE = "/upload.html";
    //用于做上传处理的action
    private static final String UPLOAD_OPERATE = "/fileUpload";

    //定义4中情况的常量（1、请求下载页面；2、请求下载目标文件；3、请求上传页面；4、请求上传文件）
    private static final int ACTION_DOWNLOAD_PAGE = 0;
    private static final int ACTION_DOWNLOAD_OPERATE = 1;
    private static final int ACTION_UPLOAD_PAGE = 2;
    private static final int ACTION_UPLOAD_OPERATE = 3;
    private static final int ACTION_ERROR = -1;

    //定义换行符
    private static final String CRLF = "\r\n";

    //定义http流的解析编码
    private static Charset charset_http_read = Charset.forName("ISO-8859-1");
    //定义默认的处理编码
    private static Charset charset = Charset.forName("UTF-8");

    //定义要下载的文件列表(key值为文件的"/父目录/文件名"(主要是为了解决多个同名文件下载问题)， value为文件对象）
    private Map<String, File> fileMap;

    //定义当前执行的操作(4种)
    private int action;

    //记录当前下载的文件url连接
    private String downloadFileUrl;



    public HttpSocketHandler(String tag, Socket socket, SocketCallback cb) throws IOException {
        super(tag, socket, cb);
    }

    @Override
    public void handlerInput() throws IOException {
        //解析http协议
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, charset_http_read)); //Charset.forName("ISO-8859-1")
        //解析http协议的startline(即第一行内容)
        String firstLine = bufferedReader.readLine();
        //如果解析出来是startline不是上传数据操作，则后续不需要解析，直接跳转到输出处理
        if (!parseRequestStartLine(firstLine)) {
            //处理上传数据
            parseUploadData(bufferedReader);
        }
        //关闭输入流
        //bufferedReader.close();
        //in.close();
    }

    @Override
    public void handlerOutput() throws IOException{

        //根据操作action类型进行对应的处理
        switch (action){
            case ACTION_DOWNLOAD_PAGE:
                //下载页面
                responseMessage("text/html;charset=UTF-8", getDownloadPage());
                break;
            case ACTION_DOWNLOAD_OPERATE:
                //下载操作
                //获取待下载的文件对象
                File file = fileMap.get(downloadFileUrl);
                if (file != null && file.exists()){
                    //文件对象不为空的
                    FileInputStream fis = new FileInputStream(file);
                    //返回数据流
                    responseStream(fis, file.getName(), file.length());
                }else{
                    responseMessage("text/plain;charset=UTF-8", "Error: 下载出错，请重新再试");
                }
                break;
            case ACTION_UPLOAD_PAGE:
                //上传页面
                responseMessage("text/html;charset=UTF-8", getUploadPage());
                break;
            case ACTION_UPLOAD_OPERATE:
                //上传操作
                //上传成功返回消息
                responseMessage("text/plain;charset=UTF-8", "Success: 上传成功");
                break;
            case ACTION_ERROR:
                //返回错误请求
                responseMessage("text/plain;charset=UTF-8", "Error: 非法的请求链接");
                break;
        }
    }

    @Override
    public void onError(Exception e) {
        //处理错误回调
        callback.error(e);
        try{
            //发送错误页面提示
            if (out != null){
                responseMessage("text/plain;charset=UTF-8", "Error");
            }
        }catch (IOException e1){
            e.printStackTrace();
        }

    }

    private void responseMessage(String content_type, String message) throws IOException{
        //定义响应消息
        StringBuilder sb = new StringBuilder();
        //添加响应成功的头部
        sb.append("HTTP/1.1 200 OK").append(CRLF);
        //添加响应类别
        sb.append("Content-Type: " + content_type).append(CRLF);
        sb.append(CRLF);
        sb.append(message).append(CRLF);
        //将内容添加到输出流
        out.write(sb.toString().getBytes(charset));
        out.flush();
        out.close();
    }

    private void responseStream(InputStream inputStream, String name, Long length) throws IOException{
        //开始下载回调
        callback.start(name, length, false);
        //输出文件下载的响应头
        StringBuilder sb = new StringBuilder();
        //响应成功
        sb.append("HTTP/1.1 200 OK").append(CRLF);
        //传输类别
        sb.append("Content-type: application/octet-stream").append(CRLF);
        //文件大小
        sb.append("Accept-Length: " + length).append(CRLF);
        //换行，下面就是主内容
        sb.append(CRLF);
        out.write(sb.toString().getBytes(charset));
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = 0;
        //定义已经传输的长度
        long transLength = 0;
        while ((count=inputStream.read(buffer)) != -1){
            out.write(buffer, 0, count);
            transLength += count;
            //回调进度值
            callback.handlerProgress(name, length, transLength, false);
        }
        //关闭输入输出流
        inputStream.close();
        out.flush();
        out.close();
        //结束
        callback.end(name, false);
    }

    /**
     * 获取下载页面源码
     * @return，返回下载页面源码字符串
     */
    private String getDownloadPage(){
        //生成下载页面的html标签
        StringBuilder sb = new StringBuilder();
        sb.append("<html>").append(CRLF);
        sb.append("<head><meta charset=\"utf-8\"><title>文件共享助手-下载</title></head>").append(CRLF);
        sb.append("<body>").append(CRLF);
        //循环遍历文件map批量生成下载链接
        Iterator iterator = fileMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = (String)iterator.next();
            sb.append("<li><a href='" + key + "'>" + fileMap.get(key).getName() + "</a></li>").append(CRLF);
        }
        sb.append("</body>").append(CRLF);
        sb.append("</html>").append(CRLF);

        return sb.toString();
    }

    /**
     * 获取上传页面html源码
     * @return，返回上传页面html源码字符串
     * @throws IOException
     */
    private String getUploadPage() throws IOException{
        StringBuilder sb = new StringBuilder();
        //获取jar包中的html资源
        InputStream inputStream = this.getClass().getResourceAsStream("/test.html");
        //下面输出具体的html文件内容
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String str = "";
        while ((str = br.readLine()) != null){
            sb.append(str).append(CRLF);
        }
        br.close();
        inputStream.close();
        return sb.toString();
    }

    /**
     * 展示文件下载界面
     * @throws IOException
     */
    private void showDownloadPage() throws IOException{
        PrintStream ps = new PrintStream(out);
        //向浏览器输出响应头信息
        ps.println("HTTP/1.1 200 OK");
        ps.println("Content-Type: text/html;charset=UTF-8");
        ps.println();//这个换行比较重要
        //下面是添加主页信息
        ps.println("<html>");
        //添加头部信息
        ps.println("<head><meta charset=\"utf-8\"><title>文件共享助手-下载</title></head>");
        ps.println("<body>");
        //循环遍历文件map批量生成下载链接
        Iterator iterator = fileMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = (String)iterator.next();
            ps.println("<li><a href='" + key + "'>" + fileMap.get(key).getName() + "</a></li>");
        }
        ps.println("</body>");
        ps.println("</html>");
        ps.flush();
        ps.close();
    }

    /**
     * 下载指定的文件
     * @throws IOException
     */
    private void downloadFile() throws IOException{
        //根据请求的下载文件连接获取文件对象
        File file = fileMap.get(downloadFileUrl);
        if (!file.exists()){
            //判断文件是否存在，并抛出异常
            throw new FileNotFoundException("待下载的文件不存在");
        }
        //开始下载回调
        callback.start(file.getName(), file.length(), false);
        //输出文件下载的响应头
        StringBuilder sb = new StringBuilder();
        //响应成功
        sb.append("HTTP/1.1 200 OK").append(CRLF);
        //传输类别
        sb.append("Content-type: application/octet-stream").append(CRLF);
        //文件大小
        sb.append("Accept-Length: " + file.length()).append(CRLF);
        //换行，下面就是主内容
        sb.append(CRLF);
        out.write(sb.toString().getBytes(charset));
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = 0;
        //定义已经传输的长度
        long transLength = 0;
        while ((count=fis.read(buffer)) != -1){
            out.write(buffer, 0, count);
            transLength += count;
            //回调进度值
            callback.handlerProgress(file.getName(), file.length(), transLength, false);
        }
        //关闭输入输出流
        fis.close();
        out.flush();
        out.close();
        //结束
        callback.end(file.getName(), false);
    }

    /**
     * 展示上传页面
     * @throws IOException
     */
    private void showUploadPage() throws IOException{
        //获取jar包中的html资源
        InputStream inputStream = this.getClass().getResourceAsStream("/upload.html");
        //定义输入输出流
        PrintStream ps = new PrintStream(out);
        //首先输出响应头
        ps.println("HTTP/1.1 200 OK");
        ps.println("Content-Type: text/html;charset:UTF-8");
        ps.println();//这个换行比较重要
        //下面输出具体的html文件内容
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String str = "";
        while ((str = br.readLine()) != null){
            ps.println(str);
        }
        br.close();
        inputStream.close();
        ps.flush();
        ps.close();
    }

    /**
     * 解析form表单中的上传数据
     * @param bufferedReader
     * @throws IOException
     */
    private void parseUploadData(BufferedReader bufferedReader) throws IOException{
        //解析请求头(Request Header),请求头与请求实体之间以一个CRLF空行区分
        Map<String, String> header = parseRequestHeader(bufferedReader);
        //获取数据长度
        long content_length = Long.parseLong(header.get("Content-Length"));
        //读取分割符
        //Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryOml2B50fQJCENuAl
        String boundary = header.get("Content-Type").split("=")[1];
        //定义解析的每一行
        String line = "";
        //开始解析具体的文件信息
        //定义已经读取长度
        long read_length = 0;
        //多文件上传的最后结束符为--boundary--
        int end_boundary_length = "--".length() + boundary.length() + "--".length();
        //解析起始的分割线
        line = bufferedReader.readLine();
        //记录长度
        read_length += line.length() + CRLF.length();
        //定义待匹配的目标字符串
        char[] target_boundary_array = (CRLF + "--" + boundary).toCharArray();
        //定义kmp算法目标串的next数组
        int[] next = Utils.getNext(target_boundary_array);
        //如果总长度-读取的长度比结束符还长的话表示还没有读取完毕
        while (content_length - read_length > end_boundary_length){
            //若不是数据结束分割线，则解析到的是开始分割符
            //------WebKitFormBoundaryAc31g86uBRjdpval
            //Content-Disposition: form-data; name="file"; filename="JavaScripté«çº§ç¨åºè®¾è®¡ï¼ç¬¬3çï¼ä¸­æ é«æ¸ å®æ´.pdf"
            //Content-Type: application/pdf
            //
            //content(具体的文件内容)
            //------WebKitFormBoundaryAc31g86uBRjdpval--
            //下面开始解析文件名
            line = bufferedReader.readLine();
            //记录长度
            read_length += line.length() + CRLF.length();
            //这里解决ISO-8859-1中文乱码问题
            String line_tmp = new String(line.getBytes(charset_http_read), charset);
            String tmp = line_tmp.split("filename=")[1].trim();
            //去除首尾的引号
            String fileName = tmp.substring(1, tmp.length()-1);
            //接下来是Content-Type和空行
            line = bufferedReader.readLine();
            read_length += line.length() + CRLF.length();
            line = bufferedReader.readLine();
            read_length += line.length() + CRLF.length();
            //解析到这里下面就是具体的文件信息了，每次读取"CRLF--boundary"个长度的内容
            //然后进行最大字符串匹配，看是否该实体块解析结束(kmp算法)
            char[] line_buff = new char[target_boundary_array.length];
            //定义写入文件的缓冲区
            char[] buff = new char[BUFFER_SIZE];
            //定义写入缓冲区的大小
            int write_buff_length = 0;
            //定义连续匹配的字符串成功的长度(会记录上一次匹配成功的长度)
            int match_length = 0;
            //定义文件输出流
            FileOutputStream fos = new FileOutputStream(new File(rootDir, fileName));
            //定义写入文件的长度
            long write_file_length = 0;
            //开始进行上传监控操作
            callback.start(fileName, content_length, true);
            while (true){
                //读取指定的长度(包含上一次匹配剩余的)进行模式匹配
                bufferedReader.read(line_buff, match_length, target_boundary_array.length-match_length);
                //记录读取长度
                read_length += target_boundary_array.length - match_length;
                //利用kmp算法,从上次匹配结束的地方开始继续匹配
                for (int i=match_length; i < target_boundary_array.length; i++) {
                    //缓存当前的匹配长度，用于后面的偏移量计算
                    int tmp_match_length = match_length;
                    //迭代计算最长的公共部分
                    while (match_length >0 && line_buff[i] != target_boundary_array[match_length]){
                        match_length = next[match_length];
                    }
                    //计算偏移量，并将前面匹配失败的写入到缓冲区
                    int offset = tmp_match_length - match_length;
                    for (int k=0; k < offset; k++){
                        //从上一次匹配成功的起始点开始，写入offset个数据
                        buff[write_buff_length++] = line_buff[k + i - tmp_match_length];
                    }
                    //上面迭代完毕后如果当前的是否相等
                    if (line_buff[i] == target_boundary_array[match_length]){
                        match_length ++;
                    }else{
                        //不相等，则将当i写入缓冲区
                        buff[write_buff_length++] = line_buff[i];
                    }
                }

                //内容遍历完成后判断match_length连续匹配到的字符串的长度，用于下一次匹配
                if (match_length > 0 && match_length < target_boundary_array.length){
                    //匹配到最后一个了，还有相同的，则表示是尾部相同，要记录当前匹配到的数据和下一次的数据进行继续对比
                    //将该块数据挪到读取缓冲区的前面
                    for (int k = 0; k < match_length; k++){
                        //将队尾的连续数据移动到队首,用于下一次继续匹配
                        line_buff[k] = line_buff[target_boundary_array.length-match_length + k];
                    }
                }else if (match_length == target_boundary_array.length){
                    //如果匹配到的长度和目标字符串的长度一致，则表示匹配成功，结束当前实体块的解析
                    //这里还要将换行符读取
                    bufferedReader.read();
                    bufferedReader.read();
                    read_length += 2;
                    //最后结束时将缓冲区中的内容写入到文件
                    //写入文件
                    writeFile(fos, buff, write_buff_length);
                    //跟新写入文件大小
                    write_file_length += write_buff_length;
                    callback.handlerProgress(fileName, content_length, write_file_length, true);
                    //清空缓冲区
                    //刷新文件输出流
                    fos.flush();
                    fos.close();
                    //结束回调
                    callback.end(fileName, true);
                    break;
                }
                //判断缓冲区内容的长度，快饱和时，将数据写入文件
                if (BUFFER_SIZE - write_buff_length <= 100){
                    //写入文件
                    writeFile(fos, buff, write_buff_length);
                    //记录写入文件的大小
                    write_file_length += write_buff_length;
                    callback.handlerProgress(fileName, content_length, write_file_length, true);
                    //清空缓冲区
                    write_buff_length = 0;
                }
            }
        }
    }

    /**
     * 将缓冲区中指定长度的数据写入到文件中
     * @param fos，文件输出流对象
     * @param buff，缓冲区对象
     * @param write_length，待写入的数据长度
     * @throws IOException
     */
    private void writeFile(FileOutputStream fos, char[] buff, int write_length) throws IOException{
        Charset cs = charset_http_read;
        CharBuffer cb = CharBuffer.allocate(buff.length);
        cb.put(buff);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        //文件写入
        fos.write(bb.array(), 0, write_length);
    }


    /**
     * 解析Http请求的StartLine，用于做http路由转发（定位url请求地址）
     * @param line，startline的内容
     * @return true：表示输入流解析结束，直接返回，false：表示继续解析余下数据
     */
    private boolean parseRequestStartLine(String line){
        //以空格分解请求行
        String[] tmp = line.split(" ");
        String method = tmp[0];     //获取是get还是post
        String url = tmp[1];        //获取请求的url
        //表示下载页面操作
        if (method.equalsIgnoreCase("get") && url.equals(DOWNLOAD_PAGE)){
            action = ACTION_DOWNLOAD_PAGE;
            return true;
        }//表示是对应的文件下载操作
        else if (method.equalsIgnoreCase("get") && fileMap.containsKey(url)) {
            action = ACTION_DOWNLOAD_OPERATE;
            downloadFileUrl = url;
            return true;
        }//表示上传页面请求操作
        else if (method.equalsIgnoreCase("get") && url.equals(UPLOAD_PAGE)){
            action = ACTION_UPLOAD_PAGE;
            return true;
        }//表示上传数据操作请求
        else if (method.equalsIgnoreCase("post") && url.equals(UPLOAD_OPERATE)){
            action = ACTION_UPLOAD_OPERATE;
            return false;
        }else if (method.equalsIgnoreCase("get") && url.equals("/favicon.ico")){
            return true;
        }else{
            //错误处理
            action = ACTION_ERROR;
            return true;
        }
    }

    /**
     * 解析请求头，并封装成map对象进行返回
     * @param bufferedReader
     * @throws IOException
     * @return,返回解析出来的请求的的map集合
     */
    private Map<String, String> parseRequestHeader(BufferedReader bufferedReader)
        throws IOException{
        Map<String, String> map = new HashMap<>();
        String line = "";
        //请求头和请求实体间以CRLF空行作为分割
        while (!(line = bufferedReader.readLine()).equals("")){
            //Host: 127.0.0.1:9000
            //Connection: keep-alive
            //Content-Length: 142658314
            //Origin: http://127.0.0.1:9000
            //User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36
            //Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryAc31g86uBRjdpval
            //Accept: */*
            //Referer: http://127.0.0.1:9000/upload.html
            //Accept-Encoding: gzip, deflate, br
            //Accept-Language: zh-CN,zh;q=0.8

            //解析出每一个头部信息
            String[] tmp = line.split(":", 2);
            //添加到map中
            map.put(tmp[0].trim(), tmp[1].trim());
        }
        return map;
    }

    /**
     * 设置文件的下载列表
     * @param files
     */
    public void setFiles(File[] files) {
        fileMap = new HashMap<>();
        for (File file : files){
            //生成文件对象的key值-> /父目录名/文件名
            String key = "/" + file.getParentFile().getName() + "/" + file.getName();
            //避免出现重复的文件
            if (!fileMap.containsKey(key)){
                fileMap.put(key, file);
            }
        }
    }
}
