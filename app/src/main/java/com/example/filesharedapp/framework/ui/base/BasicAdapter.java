package com.example.filesharedapp.framework.ui.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.filesharedapp.framework.media.entity.BaseFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义基类的适配器
 * @author yanling
 * @date 2016-09-20
 */
public abstract class BasicAdapter extends BaseAdapter{

    //定义上下文
    protected Context mContext = null;

    //定义传递的数据列表
    protected List list = null;

    //定义选中项列表
    protected ArrayList<BaseFileInfo> selectList = null;

    /**
     * 基类构造函数
     * @param context，上下文
     * @param list，显示数据列表
     * @param selectList，选中项数据列表
     */
    public BasicAdapter(Context context, List list, ArrayList<BaseFileInfo> selectList){
        this.mContext = context;
        this.list = list;
        this.selectList = selectList;
    }

    @Override
    public int getCount() {
        return list == null ? 0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null:list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
