package com.yanling.fileshared.app.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yanling.fileshared.R;
import com.yanling.fileshared.app.main.adapter.PhotoAdapter;
import com.yanling.fileshared.framework.media.MediaManager;
import com.yanling.fileshared.framework.media.entity.PhotoInfo;
import com.yanling.fileshared.framework.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片fragment
 * @author yanling
 * @date 2015-10-16
 */
public class PhotoFragment extends BaseFragment {

    //定义图片list
    private ListView photoList;
    //定义适配器
    private PhotoAdapter adapter;
    //定义查询到的图片列表
    private List<PhotoInfo> photos = new ArrayList<PhotoInfo>();
    //图片目录列表
    private List<String> bucketNames = new ArrayList<String>();


    @Override
    public void onLoadingData() {
        //得到系统中的图片
        photos = MediaManager.getInstance(this.getActivity()).getImages();
        //这里为了简便，直接在获取图片操作后取得了目录
        //所以一定要在获取图片后再取目录
        bucketNames = MediaManager.getInstance(this.getActivity()).getImageBucketNames();

    }

    @Override
    public void setContentView(ViewGroup rootView) {
        View photoView = LayoutInflater.from(this.getActivity())
                .inflate(R.layout.fragment_listview, rootView, true);
        //rootView.addView(photoView);

        initView();
        initEvent();
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
        //初始化适配器
        adapter = new PhotoAdapter(this.getActivity(), photos,bucketNames, selectList);
        //将适配器绑定到listview中
        photoList.setAdapter(adapter);
    }


}
