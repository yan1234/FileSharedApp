package com.yanling.fileshared.app.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanling.fileshared.R;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;
import com.yanling.fileshared.framework.ui.base.BasicAdapter;
import com.yanling.fileshared.framework.ui.icon.ResourceManager;
import com.yanling.fileshared.utils.md5.MD5Utils;


import java.io.File;
import java.util.ArrayList;

/**
 * 其他fragment的适配器
 * Created by yanling on 2015/11/9.
 */
public class OtherAdapter extends BasicAdapter {

    //定义文件列表
    private File[] files;

    public OtherAdapter(Context context, File[] files, ArrayList<BaseFileInfo> selectList){
        this.mContext = context;
        this.files = files;
        this.selectList = selectList;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        //if (convertView == null){
        if (!viewCacheMap.containsKey(position) || viewCacheMap.get(position) == null){
            viewHolder = new ViewHolder();
            //载入布局
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_other_list, null);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.item_other_image);
            viewHolder.name = (TextView)convertView.findViewById(R.id.item_other_name);
            viewHolder.select = (CheckBox)convertView.findViewById(R.id.item_other_select);
            convertView.setTag(viewHolder);
            viewCacheMap.put(position, convertView);
        }else{
            convertView = viewCacheMap.get(position);
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
        //viewHolder.select.setChecked(selectedFile.contains(files[position]));
        viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //添加到选择列表中
                    BaseFileInfo fileInfo = new BaseFileInfo(
                            files[position].getName(),
                            files[position].getPath(),
                            files[position].length(),
                            MD5Utils.getFileMD5(files[position]),
                            BaseFileInfo.TYPE_OTHER
                    );
                    selectList.add(fileInfo);
                }else{
                    //从选择列表中移除
                    for (int i = 0; i < selectList.size(); i++){
                        if (selectList.get(i).getPath().equals(files[position].getPath())){
                            //判断路径是否一致，一致表示是同一个文件
                            selectList.remove(i);
                            break;
                        }
                    }
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return files == null ? 0:files.length;
    }

    @Override
    public Object getItem(int position) {
        return files == null ? null:files[position];
    }

    private class ViewHolder{
        public ImageView image;
        public TextView name;
        public CheckBox select;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }
}
