package com.example.filesharedapp.framework.storage;

import android.os.Environment;

import java.io.File;

/**
 * 存储管理器，主要用于应用存储目录管理
 * @author yanling
 * @date 2016-07-13 09:33
 */
public class StorageManager {


    /**
     * 目录规划标示图
     * FileShare
     *      -- cache            //缓存
     *          -- image        //图片缓存
     */


    //定义应用的根目录名称
    private static final String APP_ROOT_NAME = "FileShare";
    //定义应用的缓存目录名称
    private static final String CACHE_NAME = "cache";
    //定义图片缓存目录
    private static final String CACHE_IMAGE_NAME = "image";

    //定义单例模式对象
    private static StorageManager storageManager;

    //定义内置存储卡的根目录
    public static StorageManager getInstance(){
        //初始化
        if (storageManager == null){
            storageManager = new StorageManager();
        }
        return storageManager;
    }

    private StorageManager(){

    }

    /**
     * 获取内置存储卡的根目录
     * @return
     */
    public File getSystemRoot(){
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取APP自身存储的根目录
     * @return
     */
    public File getAppRoot(){
        File appRoot = new File(getSystemRoot(), APP_ROOT_NAME);
        //判断文件目录是否存在
        if (!appRoot.exists()){
            //不存在即创建
            appRoot.mkdirs();
        }
        return appRoot;
    }

    /**
     * 返回应用缓存目录
     * @return
     */
    public File getAppCache(){
        File appCache = new File(getAppRoot(), CACHE_NAME);
        if (!appCache.exists()){
            appCache.mkdirs();
        }
        return appCache;
    }

    /**
     * 返回图片缓存目录
     * @return
     */
    public File getCacheOfImage(){
        File imageCache = new File(getAppCache(), CACHE_IMAGE_NAME);
        if (!imageCache.exists()){
            imageCache.mkdirs();
        }
        return imageCache;
    }

}
