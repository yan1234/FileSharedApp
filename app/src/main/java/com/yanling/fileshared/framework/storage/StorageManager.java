package com.yanling.fileshared.framework.storage;

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
     *      -- download         //下载文件存储路径
     *      -- upload           //上传文件存储路径
     */


    //定义应用的根目录名称
    private static final String APP_ROOT_NAME = "FileShare";
    //定义应用的缓存目录名称
    private static final String CACHE_NAME = "cache";
    //定义图片缓存目录
    private static final String CACHE_IMAGE_NAME = "image";
    //定义下载和上传文件目录名称
    private static final String DOWNLOAD_NAME = "download";
    private static final String UPLOAD_NAME = "upload";
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
        return getFile(getSystemRoot(), APP_ROOT_NAME);
    }

    /**
     * 返回应用缓存目录
     * @return
     */
    public File getAppCache(){
        return getFile(getAppRoot(), CACHE_NAME);
    }

    /**
     * 返回图片缓存目录
     * @return
     */
    public File getCacheOfImage(){
        return getFile(getAppCache(), CACHE_IMAGE_NAME);
    }

    /**
     * 获取下载目录
     * @return
     */
    public File getDownload(){
        return getFile(getAppRoot(), DOWNLOAD_NAME);
    }

    /**
     * 获取上传目录
     * @return
     */
    public File getUpload(){
        return getFile(getAppRoot(), UPLOAD_NAME);
    }

    /**
     * 获取系统对应的存储目录（如果不存在则创建）
     * @param parent，指定目录的父目录文件对象
     * @param filename，待创建的文件名
     * @return，返回当前文件对象
     */
    private File getFile(File parent, String filename){
        //获取指定的目录
        File file = new File(parent, filename);
        if (!file.exists()){
            file.mkdirs();
        }
        return file;
    }

}
