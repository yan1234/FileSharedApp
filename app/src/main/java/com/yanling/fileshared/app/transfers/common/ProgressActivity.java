package com.yanling.fileshared.app.transfers.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.yanling.fileshared.R;
import com.yanling.fileshared.framework.storage.StorageManager;
import com.yanling.fileshared.framework.ui.base.BaseActivity;
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
    //定义暂存数据列表(主要是为了解决线程并发数据更新造成的bug)
    private List<ProgressEntity> list = new ArrayList<>();
    //定义适配器数据
    private List<ProgressEntity> adapterList = new ArrayList<>();

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
        adapter = new ProgressAdapter(ProgressActivity.this, adapterList);
        //绑定适配器
        listView.setAdapter(adapter);
    }

    private void connectServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("10.224.68.131", 9999);
                    SimpleSocketHandler simpleSocketHandler = new SimpleSocketHandler("Client", socket, cb, SimpleSocketHandler.FLAG_HANDLER_IN);
                    simpleSocketHandler.rootDir = StorageManager.getInstance().getDownload().getPath();
                    new Thread(simpleSocketHandler).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //List<ProgressEntity> list = (List)msg.obj;
            adapterList.clear();
            adapterList.addAll(list);
            adapter.notifyDataSetChanged();
        }
    };

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
            handler.obtainMessage().sendToTarget();
        }

        @Override
        public void handlerProgress(String name, long totalSize, long transSize, boolean isIn) {
            for (ProgressEntity entity : list){
                //更改对应的进度值
                if (entity.getTitle().equals(name)){
                    entity.setDownloadSize(transSize);
                    handler.obtainMessage().sendToTarget();
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
