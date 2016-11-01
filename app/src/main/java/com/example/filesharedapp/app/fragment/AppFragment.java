package com.example.filesharedapp.app.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.fragment.adapter.AppAdapter;
import com.example.filesharedapp.framework.media.MediaManager;
import com.example.filesharedapp.framework.media.entity.AppInfo;
import com.example.filesharedapp.framework.media.entity.BaseFileInfo;
import com.example.filesharedapp.framework.ui.base.BaseFragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * app应用展示界面
 * @author yanling
 * @date 2015-10-16
 */
public class AppFragment extends BaseFragment implements AdapterView.OnItemLongClickListener{

    //最大选择数目的常量
    public static final int MAX_NUMBER_SELECTED = 3;

    //定义网格列表
    private GridView appGridView;

    //定义列表保存系统的app列表信息
    private List<AppInfo> appInfos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载布局
        view = inflater.inflate(R.layout.fragment_app, null);
        //初始化界面
        initView();
        //初始化事件
        initEvent();
        return view;
    }

    /**
     * 初始化界面
     */
    private void initView(){
        appGridView = (GridView)view.findViewById(R.id.app_gridview);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //获取手机上安装的app信息
        getAppInfos();
        //初始化适配器
        adapter = new AppAdapter(this.getActivity(), appInfos, selectList);
        //绑定适配器
        appGridView.setAdapter(adapter);
        //设置长按点击选择事件
        appGridView.setOnItemLongClickListener(this);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //先判断该项是否被选中,看集合中有没有对应的position
        if (selectList.contains(appInfos.get(position))){
            //还原被选中的状态
            view.setBackgroundColor(getResources().getColor(R.color.long_click_cancel));
            //删除集合中的值
            selectList.remove(appInfos.get(position));
        }else{
            /**
             * 这里限制最多选择3项，主要是因为二位码存储的信息有限
             * 避免选择信息过多后造成麻烦
             */
            if (selectList.size() >= MAX_NUMBER_SELECTED){
                Toast.makeText(this.getActivity(), "最多只能选择3项", Toast.LENGTH_SHORT).show();
            }else{
                //设置被选中颜色
                view.setBackgroundColor(getResources().getColor(R.color.long_click_selected));
                //添加到集合
                selectList.add(appInfos.get(position));
            }
        }
        return true;
    }

    private void getAppInfos(){
        //获取APP列表
        appInfos = MediaManager.getInstance(this.getActivity()).getAppInfos();
        //对名称按照字母排序
        Collections.sort(appInfos, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo lhs, AppInfo rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getName(), rhs.getName());
            }
        });
    }

}
