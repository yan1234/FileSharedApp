package com.example.filesharedapp.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.framework.media.entity.BaseFileInfo;
import com.example.filesharedapp.framework.media.entity.MusicInfo;
import com.example.filesharedapp.framework.ui.base.BasicAdapter;
import com.example.filesharedapp.utils.common.DateUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 音乐list适配器
 * Created by yanling on 2015/11/3.
 */
public class MusicAdapter extends BasicAdapter {


    public MusicAdapter(Context context, List<MusicInfo> list, ArrayList<BaseFileInfo> selectList){
        super(context, list, selectList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        //if (convertView == null){
        if (!viewCacheMap.containsKey(position) || viewCacheMap.get(position) == null){
            viewHolder = new ViewHolder();
            //载入布局
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_music_list, null);
            viewHolder.name = (TextView)convertView.findViewById(R.id.item_music_name);
            viewHolder.size = (TextView)convertView.findViewById(R.id.item_music_size);
            viewHolder.select = (CheckBox)convertView.findViewById(R.id.item_music_select);
            //添加到view中
            convertView.setTag(viewHolder);
            viewCacheMap.put(position, convertView);
        }else{
            convertView = viewCacheMap.get(position);
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //将数据绑定到控件中
        viewHolder.name.setText(((MusicInfo)list.get(position)).getName());
        //计算时长
        long time[] = DateUtils.millisToTime(((MusicInfo)list.get(position)).getDuration());
        //这里音乐不可能到天数，毫秒数也可以去掉，直接处理了
        String duration = "" + time[3]+":"+time[4]+":"+time[5];
        //将大小换算成MB
        float size = ((MusicInfo)list.get(position)).getSize() / (1024*1024);
        viewHolder.size.setText(duration + "  " + size + "MB");
        //判断该项是否已选择
        viewHolder.select.setChecked(selectList.contains(list.get(position)));
        //为radiobutton添加点击事件
       viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
        public TextView name;
        public TextView size;
        public CheckBox select;
    }

}
