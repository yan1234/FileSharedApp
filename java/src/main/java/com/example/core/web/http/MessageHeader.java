package com.example.core.web.http;

import java.util.List;

/**
 * 消息报头类
 * 消息报头协议包含：
 * 1、general-header(基本报头），
 * 2、request-header(请求报头），response-header(响应报头）
 * 3、entity-header(实体报头）
 * message-header=field-name":"[field-value]
 * 其中域名是大小写不敏感的，域名值开头通常是空格
 *
 * Created by yanling on 2015/11/13.
 */
public class MessageHeader {

    //定义域名值队列
    private List<Field> fields;

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * 定义头部消息的域名值对象
     */
    public class Field{
        /**
         * 定义域名值类别常量
         */
        public final static String GENERAL = "general";
        public final static String REQUEST = "request";
        public final static String RESPONSE= "response";
        public final static String ENTITY = "entity";

        //定义域名值类别，"general","request","response","entity"
        public String type;
        public String name;
        public String value;
    }
}
