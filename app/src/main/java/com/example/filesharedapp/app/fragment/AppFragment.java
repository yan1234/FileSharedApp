package com.example.filesharedapp.app.fragment;

import android.content.pm.ResolveInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.fragment.adapter.AppAdapter;
import com.example.filesharedapp.app.fragment.entity.AppInfo;
import com.example.filesharedapp.utils.systemutils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * app应用展示界面
 * @author yanling
 * @date 2015-10-16
 */
public class AppFragment extends Fragment implements AdapterView.OnItemLongClickListener{

    //定义整个页面view
    private View view;
    //定义网格列表
    private GridView appGridView;
    //定义适配器
    private AppAdapter adapter;
    //定义系统预装app列表
    private List<AppInfo> systemApps = new ArrayList<AppInfo>();
    //定义用户安装的app列表
    private List<AppInfo> customerApps = new ArrayList<AppInfo>();

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
        adapter = new AppAdapter(this.getActivity(), customerApps);
        //绑定适配器
        appGridView.setAdapter(adapter);
        //设置长按点击选择事件
        appGridView.setOnItemLongClickListener(this);
    }

    /**
     * 获取手机上安装的app信息
     */
    private void getAppInfos(){
        //得到用户的app列表
        List<ResolveInfo> cApps = AppUtils.getApps(this.getActivity(), AppUtils.CUSTOMER);
        List<ResolveInfo> sApps = AppUtils.getApps(this.getActivity(), AppUtils.SYSTEM);
        //得到用户app
        for (int i=0; i < cApps.size(); i++){
            AppInfo app = new AppInfo();
            app.setPackageName(cApps.get(i).activityInfo.packageName);
            app.setAppLabel(cApps.get(i).loadLabel(this.getActivity().getPackageManager()).toString());
            app.setIcon(cApps.get(i).loadIcon(this.getActivity().getPackageManager()));
            app.setSourceDir(cApps.get(i).activityInfo.applicationInfo.sourceDir);
            app.setIsSystem(false);
            customerApps.add(app);
        }
        //得到系统app
        for (int i=0; i < sApps.size(); i++){
            AppInfo app = new AppInfo();
            app.setPackageName(sApps.get(i).activityInfo.packageName);
            app.setAppLabel(sApps.get(i).loadLabel(this.getActivity().getPackageManager()).toString());
            app.setIcon(sApps.get(i).loadIcon(this.getActivity().getPackageManager()));
            app.setSourceDir(sApps.get(i).activityInfo.applicationInfo.sourceDir);
            app.setIsSystem(true);
            systemApps.add(app);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        view.setBackgroundColor(getResources().getColor(R.color.long_click_selected));
        adapter.setIndex(position);
        return true;
    }
}
