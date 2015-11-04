package com.example.filesharedapp.app.fragment;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.fragment.adapter.VideoAdapter;
import com.example.filesharedapp.framework.media.MediaManager;
import com.example.filesharedapp.framework.media.entity.VideoInfo;

import java.util.List;

/**
 * 视频fragment
 * @author yaning
 * @date 2015-10-16
 */
public class VideoFragment extends Fragment {


    //定义界面view
    private View view;
    //定义视频列表
    private ListView videoList = null;
    //定义适配器
    private VideoAdapter adapter = null;
    //定义数据列表
    private List<VideoInfo> videos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载界面
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
        videoList = (ListView)view.findViewById(R.id.fragment_list);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //得到视频列表
        videos = MediaManager.getInstance(this.getActivity()).getVideos();
        adapter = new VideoAdapter(this.getActivity(), videos);
        //绑定适配器
        videoList.setAdapter(adapter);
    }

}
