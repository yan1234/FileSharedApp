package com.yanling.fileshared.app.main.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanling.fileshared.R;
import com.yanling.fileshared.framework.media.entity.AppInfo;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;
import com.yanling.fileshared.framework.ui.base.BasicAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * app展示界面适配器
 * Created by yanling on 2015/10/30.
 */
public class AppAdapter extends BasicAdapter {

    //每一个item中layout布局的padding值
    public static final int LAYOUT_PADDING = 8;
    //icon图标距离顶部的值
    public static final int ICON_TO_TOP = 10;
    //标签文字距离icon图标的值
    public static final int ICON_TO_BOTTOM = 5;
    //标签文字的高度
    public static final int LABEL_HEIGHT = 40;
    //定义列数
    public static final int NUM_COLUMNS = 4;
    //定义map中存储的view的最大数
    public static final int MAX_VIEW_SIZE = 30;

    //定义屏幕的宽度，计算每一个item应该设置多宽来保证始终有4列
    private int screenWidth = 0;
    //定义变量拜师布局的宽度和图片的宽度（这里将布局和图片都设置成正方形，即宽=高）
    private int layout_width = 0;
    private int icon_width = 0;


    public AppAdapter(Context context, List<AppInfo> list, ArrayList<BaseFileInfo> selectList){
        super(context, list, selectList);
        //得到屏幕的宽度
        this.screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        //计算各个空间的宽高
        getMeasureWidth();
    }

    /**
     * 计算各个控件的尺寸
     */
    private void getMeasureWidth(){
        //这里判断下手机的屏幕是否超过1080dp
        //默认是0.8
        float weight = 0.8f;
        if (screenWidth >= 1080){
            weight = 0.6f;
        }
        //每一个item等分成4份，然后减去两边的padding，得到布局的宽度
        layout_width = screenWidth / NUM_COLUMNS - 2 * LAYOUT_PADDING;
        //布局的高度减去图片距上，距下的高度，得到图片的高度（宽度）
        icon_width = (int)((layout_width - ICON_TO_TOP - ICON_TO_BOTTOM - LABEL_HEIGHT) * weight);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (!viewCacheMap.containsKey(position) || viewCacheMap.get(position) == null){
        //if (convertView == null){
            viewHolder = new ViewHolder();
            //载入Item布局
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_app, null);
            //背景资源
            viewHolder.layout = (LinearLayout)convertView.findViewById(R.id.item_app_layout);
            //图标
            viewHolder.icon = (ImageView)convertView.findViewById(R.id.item_app_icon);
            //标签
            viewHolder.label = (TextView)convertView.findViewById(R.id.item_app_label);
            convertView.setTag(viewHolder);
            viewCacheMap.put(position, convertView);
        }else{
            convertView = viewCacheMap.get(position);
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //设置控件的宽高
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(layout_width, layout_width);
        viewHolder.layout.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(icon_width,icon_width);
        iconParams.gravity = Gravity.CENTER_HORIZONTAL;
        viewHolder.icon.setLayoutParams(iconParams);
        //设置图标
        viewHolder.icon.setImageBitmap(((AppInfo) list.get(position)).getIcon());
        //设置标签
        viewHolder.label.setText(((AppInfo)list.get(position)).getName());
        //判断当前的位置是否有选中的item
        if (selectList.contains(list.get(position))){
            viewHolder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.long_click_selected));
        }
        //清除多余的view
        if (viewCacheMap.size() > MAX_VIEW_SIZE){
            clearCacheView(convertView, (GridView)parent);
        }
        return convertView;
    }


    //定义item的view集合
    private class ViewHolder{
        //item的背景布局
        public LinearLayout layout;
        //应用图标
        public ImageView icon;
        //应用名
        public TextView label;
    }
}