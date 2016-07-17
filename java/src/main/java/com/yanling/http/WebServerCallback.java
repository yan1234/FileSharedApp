package com.yanling.http;

/**
 * 定义http处理情况回调接口
 * @author yanling
 * @date 2016-07-15 09:12
 */
public interface WebServerCallback {


    //下载操作回调
    void handleDownload();

    //展示下载页面
    void showUploadPage();

    //上传操作完成展示回调
    void showUploadComplete();

    //错误处理
    void onError(String errMsg);



}
