package com.yanling.fileshared.app.transfers.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;

import com.yanling.fileshared.R;
import com.yanling.fileshared.app.transfers.service.EventMessageForClient;
import com.yanling.fileshared.framework.Constants;
import com.yanling.fileshared.framework.storage.StorageManager;
import com.yanling.fileshared.framework.ui.base.BaseActivity;
import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 文件传输列表进度展示
 * @author yanling
 * @date 2016-12-28
 */
public class ProgressActivity extends BaseActivity {

    private static final String TAG = ProgressActivity.class.getSimpleName();

    //定义进度列表对象
    private ListView listView;
    //定义进度展示适配器
    private ProgressAdapter adapter;
    //定义适配器数据
    private List<ProgressEntity> adapterList = new ArrayList<>();

    //定义tag变量保存当前展示的进度是哪个客户端
    private String tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_list);
        //获取当前界面客户端的tag
        tag = getIntent().getStringExtra(Constants.BUNDLE_KEY_CLIENT_TAG);
        //注册EventBus事件
        EventBus.getDefault().register(this);
        //设置标题
        setTitle("进度展示");
        initView();
        initEvent();
    }

    /**
     * 初始化界面
     */
    private void initView(){
        listView = (ListView)findViewById(R.id.progress_list);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        adapter = new ProgressAdapter(ProgressActivity.this, adapterList);
        //绑定适配器
        listView.setAdapter(adapter);
    }

    public void onEventMainThread(List<EventMessageForClient> clients){
        Log.d(TAG, "收到更新的进度");
        //定义暂存数据列表(主要是为了解决线程并发数据更新造成的bug)
        //更新适配器的列表
        //判断当前是哪个客户端
        for (EventMessageForClient client : clients){
            if (tag.equals(client.getTag())){
                adapterList.clear();
                adapterList.addAll(client.getList());
                adapter.notifyDataSetChanged();
                break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销订阅事件
        EventBus.getDefault().unregister(this);
    }

}
