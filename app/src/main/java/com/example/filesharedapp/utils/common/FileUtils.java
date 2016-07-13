package com.example.filesharedapp.utils.common;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    /**
     * 将bitmap图片保存到存储目录上
     * @param bitmap，图片对象
     * @param targetPath，保存的路径
     */
    public static void saveBitmap(Bitmap bitmap, String targetPath){
        File targetFile = new File(targetPath);
        FileOutputStream fos = null;
        try {
             fos = new FileOutputStream(targetFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null){
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
