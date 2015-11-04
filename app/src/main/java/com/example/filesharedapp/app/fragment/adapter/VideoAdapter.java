package com.example.filesharedapp.app.fragment.adapter;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.framework.media.MediaManager;
import com.example.filesharedapp.framework.media.entity.VideoInfo;
import com.example.filesharedapp.utils.common.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频列表适配器
 * Created by yanling on 2015/11/4.
 */
public class VideoAdapter extends BaseAdapter{

    //上下文
    private Context mContext;
    //数据列表
    private List<VideoInfo> videos;
    //定义选择的列表
    private List<VideoInfo> selectedVideo = new ArrayList<VideoInfo>();


    public VideoAdapter(Context context, List<VideoInfo> list){
        this.mContext = context;
        this.videos = list;
    }
    @Override
    public int getCount() {
        return videos==null ? 0:videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos==null ? null:videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            //载入布局
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_video_list, null);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.item_video_image);
            viewHolder.name = (TextView)convertView.findViewById(R.id.item_video_name);
            viewHolder.size = (TextView)convertView.findViewById(R.id.item_video_size);
            viewHolder.select = (CheckBox)convertView.findViewById(R.id.item_video_select);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //将数据绑定到控件
        viewHolder.image.setImageBitmap(MediaManager.getInstance(mContext)
                .getVideoImage(videos.get(position).getId()));
        viewHolder.name.setText(videos.get(position).getName());
        //计算时长
        long time[] = DateUtils.millisToTime(videos.get(position).getDuration());
        //这里音乐不可能到天数，毫秒数也可以去掉，直接处理了
        String duration = "" + time[3]+":"+time[4]+":"+time[5];
        //将大小换算成MB
        float size = videos.get(position).getSize() / (1024*1024);
        viewHolder.size.setText(duration+"  " + size + "MB");
        //判断该项是否已选择
        if (selectedVideo.contains(videos.get(position))){
            //置为选中项
            viewHolder.select.setChecked(true);
        }
        //为radiobutton添加点击事件
        viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //添加到选择列表中
                    selectedVideo.add(videos.get(position));
                }else{
                    //从选择列表中移除
                    selectedVideo.remove(videos.get(position));
                }
            }
        });

        return convertView;
    }

    private class ViewHolder{
        public ImageView image;
        public TextView name;
        public TextView size;
        public CheckBox select;

    }

    public List<VideoInfo> getSelectedVideo() {
        return selectedVideo;
    }
}
