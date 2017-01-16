package com.yanling.fileshared;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanling.android.scanlibrary.ScanCameraActivity;
import com.yanling.android.view.progress.NoticeProgressManager;

import java.util.Timer;
import java.util.TimerTask;


public class TestActivity extends Activity implements View.OnClickListener{

    TextView text;
    Button btn;
    Button scan;
    ImageView imageView;

    private NoticeProgressManager noticeProgressManager = null;
    int progress = 0;
    Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        text = (TextView)findViewById(R.id.text);
        btn = (Button)findViewById(R.id.btn);
        scan = (Button)findViewById(R.id.scan);
        imageView = (ImageView)findViewById(R.id.imageView);
        btn.setOnClickListener(this);
        scan.setOnClickListener(this);

        Log .d("haha", ">>>>>>>>>>>启动");
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_app);
        noticeProgressManager = NoticeProgressManager.getInstance(TestActivity.this, icon, "文件共享助手");


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!noticeProgressManager.isPause()){
                            noticeProgressManager.setProgress((++progress)%100, "50 k/s", progress%100, 100);
                        }
                    }
                });
            }
        }, 1000, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        Toast.makeText(TestActivity.this, data.getExtras().get(ScanCameraActivity.SCAN_CODE).toString(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //开启wifi热点
            case R.id.btn:
                startActivityForResult(new Intent(TestActivity.this, ScanCameraActivity.class), 100);
                break;
            //扫码
            case R.id.scan:
                Toast.makeText(TestActivity.this, "点击扫一扫", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
