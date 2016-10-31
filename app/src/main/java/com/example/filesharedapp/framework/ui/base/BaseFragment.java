package com.example.filesharedapp.framework.ui.base;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.BaseAdapter;

import com.example.filesharedapp.framework.media.entity.BaseFileInfo;

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
    protected BaseAdapter basicAdapter;
    //定义选中的文件信息列表
    protected ArrayList<BaseFileInfo> selectList = new ArrayList<>();

    public BaseAdapter getBasicAdapter() {
        return basicAdapter;
    }

    public ArrayList<BaseFileInfo> getSelectList() {
        return selectList;
    }
}
