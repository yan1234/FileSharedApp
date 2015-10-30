package com.example.filesharedapp.app.fragment.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * 系统安装应用信息实体
 * Created by yanling on 2015/10/30.
 */
public class AppInfo implements Serializable{

    private static final long serialVersionUID = -5314268748715032491L;

    //应用名（标签）
    private String appLabel;

    //应用包名
    private String packageName;

    //应用图标
    private Drawable icon;

    //应用apk安装包路径
    private String sourceDir;

    //判断是否是系统预装应用，true: 是系统应用
    private boolean isSystem;

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }
}
