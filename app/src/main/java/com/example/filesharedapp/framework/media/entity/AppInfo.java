package com.example.filesharedapp.framework.media.entity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * 系统安装应用信息实体
 * Created by yanling on 2015/10/30.
 */
public class AppInfo extends BaseFileInfo implements Serializable{

    private static final long serialVersionUID = -5314268748715032491L;

    //应用名（标签）(通过基类的name继承）
    //private String appLabel;

    //应用包名
    private String packageName;

    //应用图标
    private Bitmap icon;

    //应用apk安装包路径（通过基类的path继承）
    //private String sourceDir;

    //判断是否是系统预装应用，true: 是系统应用
    private boolean isSystem;


    public AppInfo(){
        //设置文件类型
        super.setType(BaseFileInfo.TYPE_APP);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }


    public boolean isSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }
}
