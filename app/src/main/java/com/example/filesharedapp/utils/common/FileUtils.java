package com.example.filesharedapp.utils.common;

import java.io.File;
import java.io.FileFilter;

/**
 * 文件操作工具集
 * Created by yanling on 2015/11/10.
 */
public class FileUtils {


    /**
     * 获取目录的文件列表(不包含隐藏文件）
     * @return
     */
    public static File[] getFiles(File file){
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //判断文件是否隐藏
                if (!pathname.isHidden()){
                    return true;
                }
                return false;
            }
        });
        return files;
    }
}
