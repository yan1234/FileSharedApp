package com.example.filesharedapp.framework.media.entity;

import java.io.Serializable;

/**
 * 音乐信息实体
 * Created by yanling on 2015/11/2.
 */
public class MusicInfo implements Serializable{
    private static final long serialVersionUID = 1410720009422907498L;

    //资源信息id
    private int id;

    //名称
    private String name;

    //类别
    private String mimeType;

    //路径
    private String path;

    //大小
    private long size;

    public MusicInfo(){

    }

    public MusicInfo(int id, String name, String mimeType, String path, long size) {
        this.id = id;
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
