package com.example.filesharedapp.app.transfers.entity;

import com.example.filesharedapp.framework.media.entity.BaseFileInfo;

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

    //定义连接的wifi的名称
    private String ssid;

    //定义wifi连接的密码
    private String preSharedKey;

    //文件信息列表，定义list方便后期扩展发送多个文件（注意文件不能太多，可能会超出二维码存储的容量）
    private List<BaseFileInfo> files;

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPreSharedKey() {
        return preSharedKey;
    }

    public void setPreSharedKey(String preSharedKey) {
        this.preSharedKey = preSharedKey;
    }

    public List<BaseFileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<BaseFileInfo> files) {
        this.files = files;
    }
}
