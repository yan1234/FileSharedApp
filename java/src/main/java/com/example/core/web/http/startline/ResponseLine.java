package com.example.core.web.http.startline;

/**
 * http协议响应状态行
 * 协议格式：HTTP-Version Status-Code Reason-Phrase
 * Created by yanling on 2015/11/13.
 */
public class ResponseLine extends StartLine {

    //状态码
    private String status_code;

    //状态原因
    private String status_reason;

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus_reason() {
        return status_reason;
    }

    public void setStatus_reason(String status_reason) {
        this.status_reason = status_reason;
    }
}
