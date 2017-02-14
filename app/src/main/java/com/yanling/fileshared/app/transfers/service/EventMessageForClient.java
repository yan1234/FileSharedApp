package com.yanling.fileshared.app.transfers.service;

import com.yanling.fileshared.app.transfers.common.ProgressEntity;

import java.util.List;

/**
 * 定义保存连接到的client客户端的实体
 */
public class EventMessageForClient {

    //定义客户端的tag标记
    private String tag;

    //定义当前客户端连接的传输文件进度
    private List<ProgressEntity> list;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<ProgressEntity> getList() {
        return list;
    }

    public void setList(List<ProgressEntity> list) {
        this.list = list;
    }
}
