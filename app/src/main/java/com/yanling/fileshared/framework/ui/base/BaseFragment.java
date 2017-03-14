package com.yanling.fileshared.framework.ui.base;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yanling.fileshared.R;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * 定义基类的fragment
 * @author yanling
 * @date 2016-09-20
 *
 * 解决fragment快速滑动卡顿问题，主要是通过延迟加载实现
 * 主要是在fragment可见时异步加载数据，然后不可见时取消异步加载
 *
 */
public abstract class BaseFragment extends Fragment {

    //定义整个布局view
    protected View view;
    //定义适配器
    protected BasicAdapter adapter;
    //定义选中的文件信息列表
    protected ArrayList<BaseFileInfo> selectList = new ArrayList<>();

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public ArrayList<BaseFileInfo> getSelectList() {
        return selectList;
    }


    //定义变量保存数据是否载入完成
    protected boolean isDataLoaded = false;

    //定义变量保存界面是否载入完成
    protected boolean isViewCreate = false;

    //定义数据为空时显示的view
    protected View emptyView;

    //定义handler消息处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isDataLoaded = true;
            if (isViewCreate){
                //如果界面载入完成则载入实际的布局界面
                ((ViewGroup)view).removeAllViews();
                setContentView((ViewGroup)view);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //载入加载提示布局
        view = inflater.inflate(R.layout.fragment_preload, null);
        //初始化emptyView
        emptyView = getEmptyView();
        isViewCreate = true;
        //绘制界面的时候判断下数据是否载入完成
        if (isDataLoaded){
            //如果数据已经载入完毕,那么直接填充界面
            ((ViewGroup)view).removeAllViews();
            setContentView((ViewGroup)view);
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //当用户可见时
        if (getUserVisibleHint()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    onLoadingData();
                    //发送数据处理成功回调
                    handler.sendEmptyMessage(0);
                }
            }).start();
        }
    }

    /**
     * 获取自定义的EmptyView
     * @return
     */
    protected View getEmptyView(){
        View emptyView = LayoutInflater.from(this.getActivity())
                .inflate(R.layout.fragment_preload, null);
        ((TextView)emptyView.findViewById(R.id.fragment_preload_text)).setText("没有待加载的数据");
        return emptyView;
    }

    /**
     * 设置listview、gridview的emptyview
     * @param adapterView
     */
    protected void setEmptyView(AdapterView adapterView){
        ViewGroup parent = (ViewGroup)adapterView.getParent();
        //首先解除emptyView和之前父布局的关系
        if (emptyView.getParent() != null){
            ((ViewGroup)emptyView.getParent()).removeView(emptyView);
        }
        //再将emptyview添加到当前布局
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        parent.addView(emptyView, layoutParams);
        adapterView.setEmptyView(emptyView);
    }

    /**
     * 数据加载回调，该方法运行在子线程中
     */
    public abstract void onLoadingData();

    /**
     * 实际布局加载回调
     * @param rootView，当前界面根布局，通过addView加载view
     */
    public abstract void setContentView(ViewGroup rootView);
}
