package com.example.filesharedapp.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.fragment.adapter.OtherAdapter;
import com.example.filesharedapp.framework.storage.StorageManager;
import com.example.filesharedapp.framework.ui.base.BaseFragment;
import com.example.filesharedapp.utils.common.FileUtils;

import java.io.File;

/**
 * 其他Fragment
 * @author yaning
 * @date 2015-10-16
 */
public class OtherFragment extends BaseFragment {


    //定义文件操作的根目录
    public File root = StorageManager.getInstance().getSystemRoot();

    //定义界面view
    private View view;
    //定义返回图标
    private ImageView backImageView;
    //定义路径显示文本控件
    private TextView pathShowText;
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
                .inflate(R.layout.fragment_other, null);
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
        backImageView = (ImageView)view.findViewById(R.id.other_back);
        pathShowText = (TextView)view.findViewById(R.id.other_showpath);
        otherListView = (ListView)view.findViewById(R.id.fragment_list);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //获取根目录的文件列表(这里暂时不获取SD卡）
        files = FileUtils.getFiles(root);
        //对文件列表进行排序
        FileUtils.sortListFile(files);
        //初始化适配器
        adapter = new OtherAdapter(this.getActivity(), files);
        //设置父目录
        adapter.setParentFile(root);
        //绑定适配器
        otherListView.setAdapter(adapter);
        //设置item的点击事件，如果是目录则进入下一层目录
        otherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断是否是目录
                File file = adapter.getFiles()[position];
                if (file.isDirectory()) {
                    //进入下一层目录
                    //更新适配器的数据
                    updateView(file);
                }
            }
        });
        //点击返回图标监听事件
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用返回事件
                backEvent();
            }
        });
        //添加返回监听事件
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    //判断是否是返回按钮
                    if (keyCode == KeyEvent.KEYCODE_BACK){
                        //调用返回事件处理
                        backEvent();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 处理返回事件（返回按钮和返回图标）
     */
    private void backEvent(){
        //首先判断当前目录的父目录是否已经达到根部
        if (!root.equals(adapter.getParentFile())){
            //获取当前父目录的父目录的文件列表
            updateView(adapter.getParentFile().getParentFile());
        }else{
            Toast.makeText(this.getActivity(), "已到达根目录", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 更新界面
     * @param parent，当前要显示的文件列表的父目录
     */
    private void updateView(File parent){
        //首先更新文件路径显示头
        //获取当前目录相对于根目录的路径,然后将路径符换成">"
        String showPath = parent.getPath().replace(root.getPath(), "");
        pathShowText.setText(showPath);
        //然后更新ListView文件列表
        File[] tmpFiles = FileUtils.getFiles(parent);
        FileUtils.sortListFile(tmpFiles);
        adapter.setFiles(tmpFiles);
        //设置当前列表的父目录
        adapter.setParentFile(parent);
        adapter.notifyDataSetChanged();
    }




}
