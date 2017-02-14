package com.yanling.fileshared.app.transfers.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yanling.fileshared.R;
import com.yanling.fileshared.app.transfers.common.ProgressActivity;
import com.yanling.fileshared.app.transfers.common.ProgressEntity;
import com.yanling.fileshared.app.transfers.service.EventMessageForClient;
import com.yanling.fileshared.framework.Constants;
import com.yanling.fileshared.framework.media.entity.BaseFileInfo;
import com.yanling.fileshared.app.transfers.android.entity.QrcodeInfo;
import com.yanling.fileshared.framework.ui.base.BaseActivity;
import com.yanling.fileshared.utils.json.JsonUtil;
import com.yanling.fileshared.utils.md5.MD5Utils;
import com.yanling.android.scanlibrary.ScanUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class SendBetweenAppActivity extends BaseActivity implements View.OnClickListener{


    //当前连接的客户端
    private ListView lv_client;
    //定义适配器
    private BaseAdapter adapter;
    //定义当前所有客户端进度列表对象
    private List<EventMessageForClient> clients;
    //二维码图片
    private ImageView image_qrcode;
    //提示文字
    private TextView text_tip;
    //定义待发送的条码信息对象
    private QrcodeInfo qrcodeInfo = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_between_app);
        //注册EventBus消息
        EventBus.getDefault().register(this);
        //获取传递过来的条码信息
        qrcodeInfo = (QrcodeInfo)getIntent().getExtras().getSerializable(Constants.BUNDLE_KEY_QRCODEINFO);
        //初始化界面
        initView();
        //初始化事件；
        initEvent();
    }

    /**
     * 初始化界面
     */
    private void initView(){
        //设置标题头
        setTitle("扫描传输");
        //设置右边的操作标志
        header_right.setVisibility(View.VISIBLE);
        lv_client = (ListView)findViewById(R.id.send_between_app_client);
        image_qrcode = (ImageView)findViewById(R.id.send_between_app_image_qrcode);
        text_tip = (TextView)findViewById(R.id.send_between_app_text_tips);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //生成二维码并展示
        String content = packageSendInfo();
        image_qrcode.setImageBitmap(ScanUtils.encodeQRCode(content,
                250, 250));
        text_tip.setText("文件：" + content);
        header_right.setOnClickListener(this);
        //初始化数据列表
        clients = new ArrayList<>();
        //给listview绑定适配器
        adapter = new ClientAdapter();
        lv_client.setAdapter(adapter);
        //隐藏界面
        lv_client.setVisibility(View.INVISIBLE);
        //添加listview的item点击事件
        lv_client.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //点击每个客户端的传输情况进入到详细的文件进度界面
                Intent intent = new Intent(SendBetweenAppActivity.this, ProgressActivity.class);
                intent.putExtra(Constants.BUNDLE_KEY_CLIENT_TAG, clients.get(i).getTag());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销EventBus
        EventBus.getDefault().unregister(this);
    }

    /**
     * 封装二维码要传递的信息
     * @return
     */
    private String packageSendInfo(){

        List<BaseFileInfo> list = new ArrayList<>();
        //将缺失的信息补全
        for (int i = 0; i < qrcodeInfo.getFiles().size(); i++){
            BaseFileInfo fileInfo = new BaseFileInfo();
            //如果传输的是app
            if (qrcodeInfo.getFiles().get(i).getType() == BaseFileInfo.TYPE_APP){
                fileInfo.setName(qrcodeInfo.getFiles().get(i).getName() + ".apk");
            }else{
                fileInfo.setName(qrcodeInfo.getFiles().get(i).getName());
            }
            fileInfo.setPath(qrcodeInfo.getFiles().get(i).getPath());
            fileInfo.setSize(new File(fileInfo.getPath()).length());
            fileInfo.setMd5(MD5Utils.getFileMD5(new File(fileInfo.getPath())));
            fileInfo.setType(qrcodeInfo.getFiles().get(i).getType());
            list.add(fileInfo);
        }
        qrcodeInfo.setFiles(list);
        //将封装的信息转化为json字符串
        return JsonUtil.objToJson(qrcodeInfo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.header_right:
                if (clients.size() <= 0){
                    Toast.makeText(SendBetweenAppActivity.this, "当前没有客户端连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                //右边操作标示点击事件，主要用于显示和隐藏连接客户端
                if (lv_client.getVisibility() == View.VISIBLE){
                    lv_client.setVisibility(View.INVISIBLE);
                }else{
                    lv_client.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /**
     * EventBus消息接收
     * @param client
     */
    public void onEventMainThread(EventMessageForClient client){
        //更新适配器
        this.clients.add(client);
        adapter.notifyDataSetChanged();
    }


    class ClientAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return clients == null ? 0 : clients.size();
        }

        @Override
        public Object getItem(int i) {
            return clients == null ? null : clients.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (viewHolder == null){
                viewHolder = new ViewHolder();
                //载入布局
                convertView = LayoutInflater.from(SendBetweenAppActivity.this)
                        .inflate(R.layout.item_clients_list, null);
                viewHolder.image = (ImageView)convertView.findViewById(R.id.item_clients_list_image);
                viewHolder.text = (TextView)convertView.findViewById(R.id.item_clients_list_text);
                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            //设置标题
            viewHolder.text.setText(clients.get(i).getTag());
            return convertView;
        }

        class ViewHolder{
            public ImageView image;
            public TextView text;
        }
    }

}
