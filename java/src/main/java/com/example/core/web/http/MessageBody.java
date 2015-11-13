package com.example.core.web.http;

import java.util.List;

/**
 *消息实体主要包括：字符流信息，字节流信息
 * GET请求一般没有消息实体
 * POST请求消息实体如下
 * ------WebKitFormBoundarya7uEDtAhbq5YMJZP
 * Content-Disposition: form-data; name="test"  （字符流）
 * \r\n
 * xxxx
 *------WebKitFormBoundarya7uEDtAhbq5YMJZP
 *Content-Disposition: form-data; name="upload"; filename="aapt.exe"    (字节流）
 *Content-Type: application/x-msdownload
 * \r\n
 * xxxx
 *
 * Created by yanling on 2015/11/13.
 */
public class MessageBody {

    //定义消息实体列表
    private List<Body> bodys;

    public List<Body> getBodys() {
        return bodys;
    }

    public void setBodys(List<Body> bodys) {
        this.bodys = bodys;
    }

    /**
     * 定义每一个实体信息对象
     */
    class Body{
        //字符流
        public final static String CHAR = "CHAR";
        //字节流
        public final static String STREAM = "STREAM";
        //定义变量保存实体信息的类型，字符流，字节流

        public String type;
        public String name;
        public String value;
    }
}
