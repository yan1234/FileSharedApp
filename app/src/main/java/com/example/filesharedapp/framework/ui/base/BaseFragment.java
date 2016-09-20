package com.example.filesharedapp.framework.ui.base;


import android.support.v4.app.Fragment;
import android.view.View;

import com.example.filesharedapp.framework.media.entity.BaseFileInfo;

import java.util.List;

/**
 * 定义基类的fragment
 * @author yanling
 * @date 2016-09-20
 */
public class BaseFragment extends Fragment {

    //定义整个布局view
    private View view;
    //定义适配器
    private BasicAdapter basicAdapter;
    //定义选中的文件信息列表
    private List<BaseFileInfo> baseFileInfos;
}
