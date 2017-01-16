package com.yanling.fileshared.utils.common;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

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

    /**
     * 对文件目录列表进行排序（按照先目录再文件，文件名升序排列）
     * @param files
     */
    public static void sortListFile(File[] files){
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                //如果两个文件对象同为目录/文件，则按照文件名称升序排列
                if (o1.isDirectory() == o2.isDirectory()){
                    return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
                }else{
                    //通过问号表达式处理
                    //当前者为目录时，表示后者肯定为文件，直接返回-1
                    //当前者为文件是，表示后者肯定为目录，直接返回1
                    return o1.isDirectory() ? -1 : 1;
                }
            }
        });
    }
}
