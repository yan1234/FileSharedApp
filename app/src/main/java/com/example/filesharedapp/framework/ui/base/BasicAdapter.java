package com.example.filesharedapp.framework.ui.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.example.filesharedapp.framework.media.entity.BaseFileInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 定义基类的适配器
 * @author yanling
 * @date 2016-09-20
 */
public abstract class BasicAdapter extends BaseAdapter{

    //定义屏幕外之后的第几个删除数目
    //删除范围：1、数据起始到可见屏幕之上两个数据之间；2、可见屏幕下面两个数据之后到数据终止之间
    private static final int NUM_SCREEN_OUT = 3;

    //定义上下文
    protected Context mContext = null;

    //定义传递的数据列表
    protected List list = null;

    //定义选中项列表
    protected ArrayList<BaseFileInfo> selectList = null;

    //定义map缓存渲染的view item
    protected HashMap<Integer, View> viewCacheMap = new HashMap<>();



    public BasicAdapter(){

    }

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

    /**
     * 清除屏幕外面的view
     */
    public void clearCacheView(View convertView, AbsListView absListView){
        synchronized (convertView){
            //清除上面屏幕之上的数据
            for (int i = 0; i < absListView.getFirstVisiblePosition() - NUM_SCREEN_OUT; i++){
                viewCacheMap.remove(i);
            }
            //清除下面屏幕之下的数据
            for (int i = absListView.getLastVisiblePosition() + NUM_SCREEN_OUT; i < getCount(); i++){
                viewCacheMap.remove(i);
            }
        }
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

    public HashMap<Integer, View> getViewCacheMap() {
        return viewCacheMap;
    }
}
