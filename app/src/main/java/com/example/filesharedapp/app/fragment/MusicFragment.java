package com.example.filesharedapp.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.fragment.adapter.MusicAdapter;
import com.example.filesharedapp.framework.media.MediaManager;
import com.example.filesharedapp.framework.media.entity.MusicInfo;
import com.example.filesharedapp.framework.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐fragment
 * @author yaning
 * @date 2015-10-16
 */
public class MusicFragment extends BaseFragment {

    //定义界面view
    private View view;
    //定义音乐列表listview
    private ListView musicList = null;
    //定义音乐列表适配器
    private MusicAdapter adapter = null;
    //定义音乐列表数据
    private List<MusicInfo> musics = new ArrayList<MusicInfo>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //载入布局view
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
        musicList = (ListView)view.findViewById(R.id.fragment_list);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //取出音乐数据
        musics = MediaManager.getInstance(this.getActivity()).getMusics();
        //初始化适配器
        adapter = new MusicAdapter(this.getActivity(), musics, selectList);
        //绑定适配器
        musicList.setAdapter(adapter);
    }

}
