package com.example.core.web;

import com.example.core.web.http.MessageBody;
import com.example.core.web.http.MessageHeader;
import com.example.core.web.http.startline.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理http请求
 * Created by yanling on 2015/11/12.
 */
public class Request {

    //定义输入流对象
    private InputStream inputStream;

    //定义请求行
    private RequestLine requestLine;
    //定义消息报头
    private MessageHeader messageHeader;
    //定义请求实体
    private MessageBody messageBody;

    public Request(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public void request() throws IOException{
        //解析请求的url，并根据url做出对应的响应操作
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        //从缓冲区中取出数据
        /**
         * 开始解析http请求体数据
         */
        //初始化消息报头
        messageHeader = new MessageHeader();
        //定义消息报头域名值队列
        List<MessageHeader.Field> list = new ArrayList<MessageHeader.Field>();
        messageHeader.setFields(list);
        //定义每一行的数据
        String inputLine = null;
        //判断是否是开始请求行
        boolean isStartLine = true;
        //判断是否是消息报头
        boolean isHead = false;
        while ((inputLine = br.readLine()) != null){
            //首先解析第一行，获取请求类型及url路径
            //格式为：GET /upload?haha=123 HTTP/1.1
            if (isStartLine){
                //解析请求开始行
                parseStartLine(inputLine);
            }//开始解析消息报头
            else if (isHead){
                //解析消息报头域名值并添加到域名值队列中
                messageHeader.getFields().add(parseMessageHeader(inputLine));
            }//最后解析实体报头
            else{

            }
            //将请求行置为false,并开始解析消息报头
            isStartLine = false;
            isHead = true;
            //表示当前是属于请求头模块，并且遇到结束回车标记
            if (isHead && "\r\n".equals(inputLine)){
                //标志请求head结束
                isHead = false;
            }

        }
    }

    /**
     * 解析请求行
     * @param line
     */
    private void parseStartLine(String line){
        //初始化请求开始行
        requestLine = new RequestLine();
        //以空格截取
        String[] temp = line.split(" ");
        //设置请求类型
        requestLine.setMethod(temp[0].toUpperCase());
        //设置请求url
        requestLine.setUrl(temp[1]);
        //设置http协议版本
        requestLine.setHttp_version(temp[2]);
    }

    /**
     * 解析消息报头
     * @param line
     */
    private MessageHeader.Field parseMessageHeader(String line){
        MessageHeader.Field field = messageHeader.new Field();
        //解析域名,找到第一个":"分割符，然后截取之前的值
        String name=line.substring(0,line.indexOf(":"));
        //解析域名值，截取":"分割符之后的值
        String value = line.substring(line.indexOf(":")+1);
        //去掉字符串前面的空格字符
        field.name = name;
        field.value = value;
        return field;
    }
}
