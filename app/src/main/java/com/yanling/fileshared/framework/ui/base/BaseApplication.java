package com.yanling.fileshared.framework.ui.base;

import android.app.Application;

import com.yanling.fileshared.framework.cache.ImageLoaderManager;
import com.facebook.stetho.Stetho;

/**
 * 基类的Application类
 * @author yanling
 * @date 2016-07-11 11:03
 */
public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Stetho
        Stetho.initializeWithDefaults(this);
        //初始化ImageLoader
        ImageLoaderManager.init(this);
    }
}
