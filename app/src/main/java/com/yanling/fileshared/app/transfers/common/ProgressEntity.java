package com.yanling.fileshared.app.transfers.common;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * 进度展示实体
 * @author yanling
 */
public class ProgressEntity implements Serializable{


    private static final long serialVersionUID = -5416153213753063514L;


    //下载图标
    private Bitmap icon;

    //当前下载进度的标题
    private String title;

    //总大小
    private long totalSize;

    //已下载大小
    private long downloadSize;

    //当前下载对象实体的标记
    private String tag;

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
