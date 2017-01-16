package com.yanling.fileshared.app.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yanling.fileshared.R;
import com.yanling.fileshared.framework.cache.ImageLoaderManager;
import com.yanling.fileshared.framework.media.MediaManager;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;
import com.yanling.fileshared.framework.media.entity.VideoInfo;
import com.yanling.fileshared.framework.storage.StorageManager;
import com.yanling.fileshared.framework.ui.base.BasicAdapter;
import com.yanling.fileshared.utils.common.DateUtils;
import com.yanling.fileshared.utils.common.FileUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频列表适配器
 * Created by yanling on 2015/11/4.
 */
public class VideoAdapter extends BasicAdapter {


    public VideoAdapter(Context context, List list, ArrayList<BaseFileInfo> selectList){
        super(context, list, selectList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        //if (convertView == null){
        //不存在临时缓存map中或为空
        if (!viewCacheMap.containsKey(position) || viewCacheMap.get(position) == null){
            viewHolder = new ViewHolder();
            //载入布局
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_video_list, null);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.item_video_image);
            viewHolder.name = (TextView)convertView.findViewById(R.id.item_video_name);
            viewHolder.size = (TextView)convertView.findViewById(R.id.item_video_size);
            viewHolder.select = (CheckBox)convertView.findViewById(R.id.item_video_select);
            convertView.setTag(viewHolder);
            //添加到缓存中
            viewCacheMap.put(position, convertView);
        }else{
            //从缓存中取出
            convertView = viewCacheMap.get(position);
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //将数据绑定到控件
        /*viewHolder.image.setImageBitmap(MediaManager.getInstance(mContext)
                .getVideoThumbnail(videos.get(position).getPath()));*/
        //这里为了能使用ImageLoader进行图片加载，先判断是否对图片进行缓存了
        //缓存的图片就以视频实体id命名
        File cacheImg = new File(StorageManager.getInstance().getCacheOfImage(), ((VideoInfo)list.get(position)).getId()+"");
        if (!cacheImg.exists()){
            //保存bitmap
            Bitmap bitmap = MediaManager.getInstance(mContext)
                    .getVideoThumbnail(((VideoInfo)list.get(position)).getPath());
            //缓存图片
            FileUtils.saveBitmap(bitmap, cacheImg.getPath());
        }
        //通过ImageLoader加载
        final ImageView img = viewHolder.image;
        ImageLoader.getInstance().loadImage("file://" + cacheImg.getPath(),
                ImageLoaderManager.getOption(mContext),
                new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        //设置图片
                        img.setImageBitmap(loadedImage);
                    }
                });
        viewHolder.name.setText(((VideoInfo)list.get(position)).getName());
        //计算时长
        long time[] = DateUtils.millisToTime(((VideoInfo)list.get(position)).getDuration());
        //这里视频不可能到天数，毫秒数也可以去掉，直接处理了
        String duration = "" + time[3]+":"+time[4]+":"+time[5];
        //将大小换算成MB
        float size = ((VideoInfo)list.get(position)).getSize() / (1024*1024);
        viewHolder.size.setText(duration + "  " + size + "MB");
        //判断该项是否已选择
        viewHolder.select.setChecked(selectList.contains(list.get(position)));
        viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //添加到选择列表中
                    selectList.add((BaseFileInfo)list.get(position));
                }else{
                    //从选择列表中移除
                    selectList.remove(list.get(position));
                }
            }
        });
        if (viewCacheMap.size() > 30){
            clearCacheView(convertView, (ListView)parent);
        }
        return convertView;
    }

    public class ViewHolder{
        public ImageView image;
        public TextView name;
        public TextView size;
        public CheckBox select;

    }
}
