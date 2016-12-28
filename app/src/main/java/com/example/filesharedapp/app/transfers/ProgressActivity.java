package com.example.filesharedapp.app.transfers;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.app.transfers.entity.ProgressEntity;
import com.example.filesharedapp.framework.ui.base.BaseActivity;
import com.yanling.socket.SimpleSocketHandler;
import com.yanling.socket.SocketCallback;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    //定义进度数据列表对象
    private List<ProgressEntity> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_list);
        //设置标题
        setTitle("进度展示");
        initView();
        initEvent();
        //连接socket服务端
        connectServer();
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
        //获取进度数据
        list = new ArrayList<>();
        adapter = new ProgressAdapter(ProgressActivity.this, list);
        //绑定适配器
        listView.setAdapter(adapter);
    }

    private void connectServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("", 100);
                    SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler("Client", socket, cb, SimpleSocketHandler.FLAG_HANDLER_IN);
                    new Thread(simpleSocketHandler).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private SocketCallback cb = new SocketCallback() {
        @Override
        public void start(String name, long totalSize, boolean isIn) {
            //首先判断该项是否在列表中
            for (ProgressEntity entity : list){
                if (entity.getTitle().equals(name)){
                    return;
                }
            }
            //这里表示新的数据传输
            ProgressEntity entity = new ProgressEntity();
            entity.setTitle(name);
            entity.setTotalSize(totalSize);
            list.add(entity);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void handlerProgress(String name, long totalSize, long transSize, boolean isIn) {
            //更改对应的进度值
            for (int i=0; i < list.size(); i++){
                //更改对应的进度值
                if (list.get(i).getTitle().equals(name)){
                    list.get(i).setDownloadSize(transSize);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void end(String name, boolean isIn) {

        }

        @Override
        public void error(Exception e) {

        }
    };
}
