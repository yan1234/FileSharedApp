package com.example.filesharedapp.framework.media.entity;

import java.io.Serializable;

/**
 * 音乐信息实体
 * Created by yanling on 2015/11/2.
 */
public class MusicInfo extends BaseFileInfo implements Serializable{
    private static final long serialVersionUID = 1410720009422907498L;

    //资源信息id
    private int id;

    //名称
    //private String name;

    //类别
    private String mimeType;

    //路径
    //private String path;

    //大小
    //private long size;

    //时长
    private long duration;

    public MusicInfo(){
        //设置类型为音乐
        super.setType(BaseFileInfo.TYPE_MUSIC);
    }

    public MusicInfo(int id, String name, String mimeType, String path, long size,long duration) {
        this.id = id;
        this.mimeType = mimeType;
        this.duration = duration;
        super.setName(name);
        super.setPath(path);
        super.setSize(size);
        //设置类型为音乐
        super.setType(BaseFileInfo.TYPE_MUSIC);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
