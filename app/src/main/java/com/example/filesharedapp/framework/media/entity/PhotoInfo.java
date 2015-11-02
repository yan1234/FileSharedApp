package com.example.filesharedapp.framework.media.entity;

import java.io.Serializable;

/**
 * 图片信息实体
 * Created by yanling on 2015/11/2.
 */
public class PhotoInfo implements Serializable{
    private static final long serialVersionUID = 6388516619600826460L;

    //资源数据库中id
    private int id;

    //图片所在的目录
    private String bucketName;

    //图片名称
    private String name;

    //图片类型
    private String mimeType;

    //图片地址
    private String path;

    //图片大小
    private long size;

    public PhotoInfo(){

    }

    public PhotoInfo(int id, String bucketName, String name, String mimeType, String path, long size) {
        this.id = id;
        this.bucketName = bucketName;
        this.name = name;
        this.mimeType = mimeType;
        this.path = path;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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
}
