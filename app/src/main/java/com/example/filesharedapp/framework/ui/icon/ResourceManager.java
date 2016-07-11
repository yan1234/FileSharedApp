package com.example.filesharedapp.framework.ui.icon;


import android.content.Context;

/**
 * 静态资源管理类，主要包含常用的图标drawable，字符串string等等资源
 * @author yanling
 * @date 2016-07-11 16:52
 */
public class ResourceManager {


    /**
     * 文件类型管理
     */

    //文档类型管理
    public static final String[] FILE_TYPE_DOCUMENT =
            {".txt", ".md", ".html", ".log", ".doc", ".xlsx", ".ppt", ".pdf"};
    //图片类型管理
    public static final String[] FILE_TYPE_PHOTO =
            {".jpg", ".jpeg", ".png", ".bmp", ".webp", ".gif"};
    //音乐类型管理
    public static final String[] FILE_TYPE_MUSIC =
            {".mp3", ".aac", ".wma"};
    //视频类型管理
    public static final String[] FILE_TYPE_VIDEO =
            {".mp4", ".rmvb", ".wmv", ".mkv", ".avi", ".mov"};




    /**
     * 图标管理
     */

    //目录图标
    public static final String ICON_DIR = "drawable/icon_dir";
    //文档图标
    public static final String ICON_DOCUMENT = "drawable/icon_document";
    //音乐文件图标
    public static final String ICON_MUSIC = "drawable/icon_music";
    //图片文件图标
    public static final String ICON_PHOTO = "drawable/icon_photo";
    //视频文件图标
    public static final String ICON_VIDEO = "drawable/icon_video";
    //app应用图标
    public static final String ICON_APP = "drawable/icon_app";
    //未知文件图标
    public static final String ICON_UNKNOWN = "drawable/icon_unknown";




    /**
     * 通过资源的类型名和name名获取资源id
     * @param context，上下文
     * @param typeAndName，类型名And名称
     * @return，返回资源id
     */
    public static int getResourceId(Context context, String typeAndName){
        return context.getResources().getIdentifier(typeAndName, null, context.getPackageName());
    }


    /**
     * 根据文件名(后缀）设置对应的图标icon
     * @param context，上下文
     * @param filename，对应的文件名
     * @return，返回资源id
     */
    public static int getIconType(Context context, String filename){
        /**
         * 判断文件的后缀名是否是可以显示的文件名称
         */
        //先遍历文档类型
        for (String str : FILE_TYPE_DOCUMENT){
            if (filename.endsWith(str)){
                return getResourceId(context, ICON_DOCUMENT);
            }
        }
        //照片类型
        for (String str : FILE_TYPE_PHOTO){
            if (filename.endsWith(str)){
                return getResourceId(context, ICON_PHOTO);
            }
        }
        //音频类型
        for (String str : FILE_TYPE_MUSIC){
            if (filename.endsWith(str)){
                return getResourceId(context, ICON_MUSIC);
            }
        }
        //视频类型
        for (String str : FILE_TYPE_VIDEO){
            if (filename.endsWith(str)){
                return getResourceId(context, ICON_VIDEO);
            }
        }
        //设置APP应用图标
        if (filename.endsWith(".apk")){
            return  getResourceId(context, ICON_APP);
        }
        //全部遍历完没有找到就显示未知
        return getResourceId(context, ICON_UNKNOWN);

    }

}
