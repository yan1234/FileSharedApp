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
import com.example.filesharedapp.framework.ui.icon.ResourceManager;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
    //通过map缓存view
    private HashMap<Integer, View> viewMap = null;
    //定义已选择的文件列表
    private List<File> selectedFile = null;
    //定义变量保存父目录
    private File parentFile;

    public OtherAdapter(Context context, File[] files){
        this.mContext = context;
        this.files = files;
        //初始化数据
        viewMap = new HashMap<>();
        selectedFile = new ArrayList<>();
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
        //if (convertView == null){
        if (!viewMap.containsKey(position) || viewMap.get(position) == null){
            viewHolder = new ViewHolder();
            //载入布局
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_other_list, null);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.item_other_image);
            viewHolder.name = (TextView)convertView.findViewById(R.id.item_other_name);
            viewHolder.select = (CheckBox)convertView.findViewById(R.id.item_other_select);
            convertView.setTag(viewHolder);
            viewMap.put(position, convertView);
        }else{
            convertView = viewMap.get(position);
            viewHolder = (ViewHolder)convertView.getTag();
        }
        /**
         *事件与界面的绑定
         */
        //判断该项是文件还是目录，显示对于的图片
        if (files[position].isDirectory()){
            viewHolder.image.setImageResource(ResourceManager.getResourceId(mContext, ResourceManager.ICON_DIR));
            //隐藏文件目录的选择框
            viewHolder.select.setVisibility(View.INVISIBLE);
        }//显示文件图标
        else{
            //根据文件后缀显示不同文件图标
            viewHolder.image.setImageResource(ResourceManager.getIconType(mContext, files[position].getName()));
        }
        //显示文件名
        viewHolder.name.setText(files[position].getName());
        //设置选择事件
        viewHolder.select.setChecked(selectedFile.contains(files[position]));
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

    public File getParentFile() {
        return parentFile;
    }

    public void setParentFile(File parentFile) {
        this.parentFile = parentFile;
    }
}
