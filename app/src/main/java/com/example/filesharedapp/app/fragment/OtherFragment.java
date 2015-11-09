package com.example.filesharedapp.app.fragment;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.fragment.adapter.OtherAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 其他Fragment
 * @author yaning
 * @date 2015-10-16
 */
public class OtherFragment extends Fragment {

    //定义界面view
    private View view;
    //定义文件列表布局
    private ListView otherListView;
    //定义适配器
    private OtherAdapter adapter;
    //定义文件数据
    private File[] files;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //载入view
        view = inflater.from(this.getActivity())
                .inflate(R.layout.fragment_listview, null);
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
        otherListView = (ListView)view.findViewById(R.id.fragment_list);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //获取根目录的文件列表(这里暂时不获取SD卡）
        files = Environment.getExternalStorageDirectory().listFiles();
        //初始化适配器
        adapter  = new OtherAdapter(this.getActivity(), files);
        //绑定适配器
        otherListView.setAdapter(adapter);
        //设置item的点击事件，如果是目录则进入下一层目录
        otherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断是否是目录
                File file = adapter.getFiles()[position];
                if (file.isDirectory() && file.listFiles().length != 0){
                    //进入下一层目录
                    //更新适配器的数据
                    adapter.setFiles(file.listFiles());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


}
