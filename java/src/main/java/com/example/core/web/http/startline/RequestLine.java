package com.example.core.web.http.startline;

/**
 * http协议请求行
 * 协议格式：Method Request-URI HTTP-Version
 * Created by yanling on 2015/11/13.
 */
public class RequestLine extends StartLine {

    public final static String GET="GET";
    public final static String POST="POST";

    //请求方法
    private String method;
    //请求的url
    private String url;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
