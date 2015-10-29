package com.example.filesharedapp.app.transfers.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 传输信息实体，包括socket连接的端口号，传输文件信息
 * Created by yanling on 2015/10/28.
 */
public class QrcodeInfo implements Serializable{

    private static final long serialVersionUID = 3651614906726502582L;

    //socket连接的端口号
    private int hostPort;

    //文件信息列表，定义list方便后期扩展发送多个文件（注意文件不能太多，可能会超出二维码存储的容量）
    private List<FileInfo> files;

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }
}
