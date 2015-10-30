package com.example.filesharedapp.app.fragment.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.fragment.entity.AppInfo;

import java.util.HashMap;
import java.util.List;

/**
 * app展示界面适配器
 * Created by yanling on 2015/10/30.
 */
public class AppAdapter extends BaseAdapter{

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

    //定义上下文
    private Context mContext;
    //定义app数据列表
    private List<AppInfo> apps;
    //定义屏幕的宽度，计算每一个item应该设置多宽来保证始终有4列
    private int screenWidth = 0;
    //定义变量拜师布局的宽度和图片的宽度（这里将布局和图片都设置成正方形，即宽=高）
    private int layout_width = 0;
    private int icon_width = 0;

    //定义index记录选中的位置
    private int index = -1;
    //private HashMap<Integer, View> viewMap = new HashMap<Integer, View>();

    public AppAdapter(Context context, List<AppInfo> apps){
        this.mContext = context;
        this.apps = apps;
        //得到屏幕的宽度
        this.screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        //计算各个空间的宽高
        getMeasureWidth();
    }

    /**
     * 计算各个控件的尺寸
     */
    private void getMeasureWidth(){
        //每一个item等分成4份，然后减去两边的padding，得到布局的宽度
        layout_width = screenWidth / NUM_COLUMNS - 2 * LAYOUT_PADDING;
        //布局的高度减去图片距上，距下的高度，得到图片的高度（宽度）
        icon_width = (layout_width - ICON_TO_TOP - ICON_TO_BOTTOM - LABEL_HEIGHT) * 4/5;
    }

    @Override
    public int getCount() {
        return apps == null ? 0:apps.size();
    }

    @Override
    public Object getItem(int position) {
        return apps == null ? null:apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setIndex(int position){
        this.index = position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        //if (!viewMap.containsKey(position) || viewMap.get(position) == null){
        if (convertView == null){
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
        }else{
            //convertView = viewMap.get(position);
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //设置控件的宽高
        GridView.LayoutParams layoutParams = new GridView.LayoutParams(layout_width, layout_width);
        viewHolder.layout.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(icon_width,icon_width);
        iconParams.gravity = Gravity.CENTER_HORIZONTAL;
        viewHolder.icon.setLayoutParams(iconParams);
        //设置图标
        viewHolder.icon.setImageDrawable(apps.get(position).getIcon());
        //设置标签
        viewHolder.label.setText(apps.get(position).getAppLabel());
        if (index == position){
            viewHolder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.long_click_selected));
        }else{
            viewHolder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.long_click_cancel));
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
