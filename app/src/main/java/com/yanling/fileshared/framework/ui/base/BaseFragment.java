package com.yanling.fileshared.framework.ui.base;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.BaseAdapter;

import com.yanling.fileshared.framework.media.entity.BaseFileInfo;

import java.util.ArrayList;

/**
 * 定义基类的fragment
 * @author yanling
 * @date 2016-09-20
 */
public class BaseFragment extends Fragment {

    //定义整个布局view
    protected View view;
    //定义适配器
    protected BasicAdapter adapter;
    //定义选中的文件信息列表
    protected ArrayList<BaseFileInfo> selectList = new ArrayList<>();

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public ArrayList<BaseFileInfo> getSelectList() {
        return selectList;
    }
}
