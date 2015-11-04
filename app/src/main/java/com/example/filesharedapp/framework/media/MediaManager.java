package com.example.filesharedapp.framework.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.example.filesharedapp.framework.media.entity.MusicInfo;
import com.example.filesharedapp.framework.media.entity.PhotoInfo;
import com.example.filesharedapp.framework.media.entity.VideoInfo;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体管理类，主要包括管理手机上的图片、音乐、视频等多媒体
 * Created by yanling on 2015/11/2.
 */
public class MediaManager {

    //定义单例模式对象
    private static MediaManager mediaManager;
    //上下文
    private Context mContext;
    //定义ContentResolver对象
    private ContentResolver contentResolver;
    //定义变量保存图片目录的分类
    private List<String> imageBucketNames = new ArrayList<String>();

    /**
     * 获取媒体管理对象的单例
     * @param context，上下文
     * @return
     */
    public static MediaManager getInstance(Context context){
        if (mediaManager == null){
            mediaManager = new MediaManager(context);
        }
        return mediaManager;
    }

    private MediaManager(Context context){
        this.mContext = context;
        this.contentResolver = mContext.getContentResolver();
    }

    /**
     * 获取手机中的图片
     * @return，图片信息列表
     */
    public List<PhotoInfo> getImages(){
        List<PhotoInfo> photos = new ArrayList<PhotoInfo>();
        //查询出手机上的图片
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,   //查询图片的uri
                null,                 //查询到的列，类似于数据中字段，null表示所有
                null,                 //过滤条件，类似于数据库语句中的where,null表示不过滤
                null,                 //多个过滤条件
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME);//按目录排序，类似于数据库语句中的order by, null表示不操作
        while (cursor.moveToNext()){
            //将取到的信息封装到图片实体中
            PhotoInfo photo = new PhotoInfo(
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)),
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
            photos.add(photo);
            //判断是否包含当前目录
            if (!imageBucketNames.contains(photo.getBucketName())){
                imageBucketNames.add(photo.getBucketName());
            }
        }
        //关闭查询器
        if (cursor != null){
            cursor.close();
        }

        return photos;
    }

    /**
     * 根据图片的资源id获取图片
     * @param id
     * @return，返回bitmap
     */
    public Bitmap getImages(int id){
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                contentResolver,
                id,
                MediaStore.Images.Thumbnails.MICRO_KIND,
                null);      //BitmapFactory.Options
        return bitmap;
    }

    /**
     * 获取音乐列表
     * @return，音乐信息列表
     */
    public List<MusicInfo> getMusics(){
        List<MusicInfo> musics = new ArrayList<MusicInfo>();
        //查询出手机上的音乐
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,   //查询音乐的uri
                null,                 //查询到的列，类似于数据中字段，null表示所有
                null,                 //过滤条件，类似于数据库语句中的where,null表示不过滤
                null,                 //多个过滤条件
                null);                //排序，类似于数据库语句中的order by, null比奥斯不操作
        while (cursor.moveToNext()){
            //取出音乐信息实体
            MusicInfo music = new MusicInfo(
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)),
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            musics.add(music);

        }
        if (cursor != null){
            cursor.close();
        }
        return musics;
    }

    /**
     * 获取视频列表
     * @return
     */
    public List<VideoInfo> getVideos(){
        List<VideoInfo> videos = new ArrayList<VideoInfo>();
        //查询出手机上的视频
        Cursor cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,   //查询视频的uri
                null,                 //查询到的列，类似于数据中字段，null表示所有
                null,                 //过滤条件，类似于数据库语句中的where,null表示不过滤
                null,                 //多个过滤条件
                null);                //排序，类似于数据库语句中的order by, null表示不操作
        while (cursor.moveToNext()){
            //取出视频信息实体
            VideoInfo video = new VideoInfo(
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)),
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)),
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
            videos.add(video);
        }
        if (cursor != null){
            cursor.close();
        }
        return videos;
    }

    /**
     * 根据资源id获取视频缩略图
     * @param id
     * @return
     */
    public Bitmap getVideoImage(int id){
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                contentResolver,
                id,
                MediaStore.Video.Thumbnails.MICRO_KIND,
                null);      //BitmapFactory.Options
        return bitmap;
    }

    public List<String> getImageBucketNames() {
        return imageBucketNames;
    }
}
