package com.example.filesharedapp.app.fragment;

import android.content.pm.ResolveInfo;
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
import com.example.filesharedapp.app.fragment.entity.AppInfo;
import com.example.filesharedapp.utils.systemutils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * app应用展示界面
 * @author yanling
 * @date 2015-10-16
 */
public class AppFragment extends Fragment implements AdapterView.OnItemLongClickListener{

    //最大选择数目的常量
    public static final int MAX_NUMBER_SELECTED = 3;


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
        //先清空集合
        customerApps.clear();
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
        //先清空集合
        systemApps.clear();
        for (int i=0; i < sApps.size(); i++){
            AppInfo app = new AppInfo();
            app.setPackageName(sApps.get(i).activityInfo.packageName);
            app.setAppLabel(sApps.get(i).loadLabel(this.getActivity().getPackageManager()).toString());
            app.setIcon(sApps.get(i).loadIcon(this.getActivity().getPackageManager()));
            app.setSourceDir(sApps.get(i).activityInfo.applicationInfo.sourceDir);
            app.setIsSystem(true);
            systemApps.add(app);
        }
        //对名称按照字母排序
        Collections.sort(customerApps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo lhs, AppInfo rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getAppLabel(),rhs.getAppLabel());
            }
        });
        Collections.sort(systemApps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo lhs, AppInfo rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getAppLabel(),rhs.getAppLabel());
            }
        });
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //先判断该项是否被选中,看集合中有没有对应的position
        if (adapter.indexSet.contains(position)){
            //还原被选中的状态
            view.setBackgroundColor(getResources().getColor(R.color.long_click_cancel));
            //删除集合中的值
            adapter.indexSet.remove(position);
        }else{
            /**
             * 这里限制最多选择3项，主要是因为二位码存储的信息有限
             * 避免选择信息过多后造成麻烦
             */
            if (adapter.indexSet.size() >= MAX_NUMBER_SELECTED){
                Toast.makeText(this.getActivity(), "最多只能选择3项", Toast.LENGTH_SHORT).show();
            }else{
                //设置被选中颜色
                view.setBackgroundColor(getResources().getColor(R.color.long_click_selected));
                //添加到集合
                adapter.indexSet.add(position);
            }
        }
        return true;
    }

}
