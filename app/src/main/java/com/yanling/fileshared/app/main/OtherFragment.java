package com.yanling.fileshared.app.main;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanling.fileshared.R;
import com.yanling.fileshared.app.main.adapter.OtherAdapter;
import com.yanling.fileshared.framework.storage.StorageManager;
import com.yanling.fileshared.framework.ui.base.BaseFragment;
import com.yanling.fileshared.utils.common.FileUtils;

import java.io.File;

/**
 * 其他Fragment
 * @author yaning
 * @date 2015-10-16
 */
public class OtherFragment extends BaseFragment
        implements AdapterView.OnItemClickListener, View.OnClickListener,View.OnKeyListener{


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
    //定义文件数据
    private File[] files;
    //定义当前文件列表的父文件对象(初始值为root目录)
    private File parentFile = root;

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
        files = FileUtils.getFiles(parentFile);
        //对文件列表进行排序
        FileUtils.sortListFile(files);
        //初始化适配器
        adapter = new OtherAdapter(this.getActivity(), files, selectList);
        //绑定适配器
        otherListView.setAdapter(adapter);
        //设置item的点击事件，如果是目录则进入下一层目录
        otherListView.setOnItemClickListener(this);
        //点击返回图标监听事件
        backImageView.setOnClickListener(this);
        //添加返回监听事件
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
    }

    /**
     * 处理返回事件（返回按钮和返回图标）
     */
    private void backEvent(){
        //首先判断当前目录的父目录是否已经达到根部
        if (!root.equals(parentFile)){
            //获取当前父目录的父目录的文件列表
            updateView(parentFile.getParentFile());
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
        ((OtherAdapter)adapter).setFiles(tmpFiles);
        //记录父目录
        parentFile = parent;
        files = tmpFiles;
        //清除缓存的viewMap
        adapter.getViewCacheMap().clear();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //判断是否是目录
        File file = (File)adapterView.getItemAtPosition(position);
        if (file.isDirectory()) {
            //进入下一层目录
            //更新适配器的数据
            updateView(file);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.other_back:
                //顶部返回按钮点击事件
                backEvent();
                break;
        }
    }

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
}
