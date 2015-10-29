package com.example.filesharedapp.app.transfers.entity;

import java.io.Serializable;

/**
 * 传输文件信息实体
 * Created by yanling on 2015/10/28.
 */
public class FileInfo implements Serializable{

    private static final long serialVersionUID = 6905472348213952006L;

    //文件名
    private String fileName;

    //文件所在路径
    private String path;

    //文件大小
    private long size;

    //文件类型（1:app, 2:图片，3：视频，4：音乐，5：其他）
    private int type;

    //文件的md5值，主要用于校验文件的完整性
    private String md5;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
