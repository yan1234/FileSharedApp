package com.example.filesharedapp.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.framework.media.entity.MusicInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐list适配器
 * Created by yanling on 2015/11/3.
 */
public class MusicAdapter extends BaseAdapter{


    //定义上下文
    private Context mContext;
    //数据列表
    private List<MusicInfo> musics;
    //定义已选择的列表
    private List<MusicInfo> selectedMusic = new ArrayList<MusicInfo>();


    public MusicAdapter(Context context, List<MusicInfo> list){
        this.mContext = context;
        this.musics = list;
    }

    @Override
    public int getCount() {
        return musics == null ? 0: musics.size();
    }

    @Override
    public Object getItem(int position) {
        return musics==null ? null:musics.get(position);
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
                    .inflate(R.layout.item_music_list, null);
            viewHolder.name = (TextView)convertView.findViewById(R.id.item_music_name);
            viewHolder.size = (TextView)convertView.findViewById(R.id.item_music_size);
            viewHolder.select = (CheckBox)convertView.findViewById(R.id.item_music_select);
            //添加到view中
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //将数据绑定到控件中
        viewHolder.name.setText(musics.get(position).getName());
        //将大小换算成MB
        float size = musics.get(position).getSize() / (1024*1024);
        viewHolder.size.setText("" + size + "MB");
        //判断该项是否已选择
        if (selectedMusic.contains(musics.get(position))){
            //置为选中项
            viewHolder.select.setChecked(true);
        }
        //为radiobutton添加点击事件
        viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //添加到选择列表中
                    selectedMusic.add(musics.get(position));
                }else{
                    //从选择列表中移除
                    selectedMusic.remove(musics.get(position));
                }
            }
        });
        return convertView;
    }

    private class ViewHolder{
        public TextView name;
        public TextView size;
        public CheckBox select;
    }

    public List<MusicInfo> getSelectedMusic() {
        return selectedMusic;
    }
}
