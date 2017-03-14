package com.yanling.fileshared.app.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yanling.fileshared.R;
import com.yanling.fileshared.app.main.adapter.MusicAdapter;
import com.yanling.fileshared.framework.media.MediaManager;
import com.yanling.fileshared.framework.media.entity.MusicInfo;
import com.yanling.fileshared.framework.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐fragment
 * @author yaning
 * @date 2015-10-16
 */
public class MusicFragment extends BaseFragment{

    //定义音乐列表listview
    private ListView musicList = null;
    //定义音乐列表数据
    private List<MusicInfo> musics = new ArrayList<MusicInfo>();


    @Override
    public void onLoadingData() {
        //取出音乐数据
        musics = MediaManager.getInstance(this.getActivity()).getMusics();
    }

    @Override
    public void setContentView(ViewGroup rootView) {
        View musicView = LayoutInflater.from(this.getActivity())
                .inflate(R.layout.fragment_listview, rootView, true);

        initView();
        initEvent();
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
        //初始化适配器
        adapter = new MusicAdapter(this.getActivity(), musics, selectList);
        //绑定适配器
        musicList.setAdapter(adapter);
    }
}
