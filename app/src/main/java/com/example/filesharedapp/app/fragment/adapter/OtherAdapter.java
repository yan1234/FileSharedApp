package com.example.filesharedapp.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.filesharedapp.R;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 其他fragment的适配器
 * Created by yanling on 2015/11/9.
 */
public class OtherAdapter extends BaseAdapter{

    //定义上下文
    private Context mContext;
    //定义文件列表
    private File[] files;
    //定义已选择的文件列表
    private List<File> selectedFile = new ArrayList<File>();

    public OtherAdapter(Context context, File[] files){
        this.mContext = context;
        this.files = files;
    }

    @Override
    public int getCount() {
        return files==null ? 0:files.length;
    }

    @Override
    public Object getItem(int position) {
        return files==null ? null:files[position];
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
                    .inflate(R.layout.item_other_list, null);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.item_other_image);
            viewHolder.name = (TextView)convertView.findViewById(R.id.item_other_name);
            viewHolder.select = (CheckBox)convertView.findViewById(R.id.item_other_select);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        /**
         *事件与界面的绑定
         */
        //判断该项是文件还是目录，显示对于的图片
        if (files[position].isDirectory()){
            viewHolder.image.setImageResource(R.mipmap.ic_launcher);
        }//显示文件图标
        else{
            viewHolder.image.setImageResource(R.mipmap.ic_launcher);
        }
        //显示文件名
        viewHolder.name.setText(files[position].getName());
        //设置选择事件
        viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //添加到选择列表中
                    selectedFile.add(files[position]);
                }else{
                    //从选择列表中移除
                    selectedFile.remove(files[position]);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder{
        public ImageView image;
        public TextView name;
        public CheckBox select;
    }

    public List<File> getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(List<File> selectedFile) {
        this.selectedFile = selectedFile;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }
}