package com.example.core.web.http.startline;

/**
 * http协议初始行基类
 * 主要分为请求行和响应行
 * Created by yanling on 2015/11/13.
 */
public class StartLine {
    //http版本，主要是1.0和1.1
    private String http_version;

    public String getHttp_version() {
        return http_version;
    }

    public void setHttp_version(String http_version) {
        this.http_version = http_version;
    }
}
