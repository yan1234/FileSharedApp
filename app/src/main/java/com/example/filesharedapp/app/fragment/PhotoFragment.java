package com.example.filesharedapp.app.fragment;


import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.filesharedapp.R;


/**
 * 图片fragment
 * @author yanling
 * @date 2015-10-16
 */
public class PhotoFragment extends Fragment {

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media._ID
    };

    //定义界面的view对象
    private View view;
    //定义图片list
    private ListView photoList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载布局
        view = inflater.inflate(R.layout.fragment_listview, null);
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
        photoList = (ListView)view.findViewById(R.id.fragment_list);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){

    }


}
