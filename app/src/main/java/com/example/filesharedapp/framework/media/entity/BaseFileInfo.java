package com.example.filesharedapp.framework.media.entity;

import java.io.Serializable;

/**
 * 基类文件信息实体，包含一个文件最基本的信息，主要用于文件传输的最小信息单元
 * Created by yanling on 2015/10/28.
 */
public class BaseFileInfo implements Serializable{

    private static final long serialVersionUID = 6905472348213952006L;

    //文件类型（1:app, 2:图片，3：视频，4：音乐，5：其他）
    public static final int TYPE_APP = 1;
    public static final int TYPE_PHOTO = 2;
    public static final int TYPE_MUSIC = 3;
    public static final int TYPE_VEDIO = 4;
    public static final int TYPE_OTHER = 5;

    //文件名
    private String name;

    //文件所在路径
    private String path;

    //文件大小
    private long size;

    //文件类型
    private int type;

    //文件的md5值，主要用于校验文件的完整性
    private String md5;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
