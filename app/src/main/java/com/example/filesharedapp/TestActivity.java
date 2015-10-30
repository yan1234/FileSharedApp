package com.example.filesharedapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filesharedapp.app.transfers.SendShowActivity;
import com.example.filesharedapp.framework.wifi.WifiController;
import com.example.filesharedapp.utils.systemutils.AppUtils;
import com.example.scanlibrary.ScanCameraActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TestActivity extends Activity implements View.OnClickListener{

    TextView text;
    Button btn;
    Button scan;
    ImageView imageView;

    WifiController wifiController;

    ServerSocket server = null;

    private List<ResolveInfo> mApps;
    private PackageManager pm;

    private static final String[]STORE_IMAGES={
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
    };

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

        wifiController = new WifiController(TestActivity.this);
        initApp();
    }

    private void initApp()
    {

        this.mApps = AppUtils.getApps(TestActivity.this, AppUtils.SYSTEM);

        //Toast.makeText(TestActivity.this, r1.loadLabel(pm), Toast.LENGTH_LONG).show();
        /*Collections.sort(this.mApps, new Comparator() {
            @Override
            public int compare(ResolveInfo paramResolveInfo1, ResolveInfo paramResolveInfo2) {
                PackageManager localPackageManager = TestActivity.this.getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(paramResolveInfo1.loadLabel(localPackageManager).toString(), paramResolveInfo2.loadLabel(localPackageManager).toString());
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        Toast.makeText(TestActivity.this, data.getExtras().get(ScanCameraActivity.SCAN_CODE).toString(), Toast.LENGTH_SHORT).show();

    }

    private void getImage(){
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), Integer.parseInt(list.get(position).getId()), Thumbnails.MICRO_KIND, null);

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                appendPath(id).build();
        ContentResolver resolver = getContentResolver();
        FileUtil file = new FileUtil();

        try {
            mContent = file.readInputStream(resolver.openInputStream(Uri.parse(uri.toString())));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap = file.getBitmapFromBytes(mContent, null);
        iv.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //开启wifi热点
            case R.id.btn:
                Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        STORE_IMAGES);
                while (cursor.moveToNext()){
                    String id = cursor.getString(3);
                    String displayname = cursor.getString(0);
                    String haha = "123";
                }
                cursor.close();
                /*try {
                    String str1 = getPackageManager().getApplicationInfo(mApps.get(0).activityInfo.packageName, 0).sourceDir;
                    File appFile = new File(Environment.getExternalStorageDirectory()+ File.separator + "music.apk");
                    File sourceApk = new File(str1);
                    try {
                        appFile.createNewFile();
                        FileInputStream fis = new FileInputStream(sourceApk);
                        FileOutputStream fos = new FileOutputStream(appFile);
                        byte[] data = new byte[1024];
                        int length = -1;
                        while((length=fis.read(data)) != -1){
                            fos.write(data, 0, length);
                        }
                        fos.flush();
                        fos.close();
                        fis.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    Log.i("Tag", ">>>>>"+str1);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }*/
                //startActivity(new Intent(TestActivity.this, SendShowActivity.class));
                //wifiController.startWifiAp("1234", "12345678", true);
                /*String ip = wifiController.getWifiAddress();
                final String host = ip.substring(0,ip.lastIndexOf(".")) + ".1";
                new Thread(){
                    public void run(){
                        try {

                            Socket socket = new Socket(host, 9000);
                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                            String str = br.readLine();
                            Log.i("haha", ">>>>>>Client" + str);
                            pw.print(str);
                            pw.flush();
                            br.close();
                            pw.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();*/

                /*//先打开wifi连接
                wifiController.setWifiStatus(true);
                //开启后延时一会
                while (wifiController.wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING){
                    //没过100毫秒检查一次
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                wifiController.connectWifi("test", "12345678");*/
                /*
                text.setText(ip);
                Bitmap bitmap = ScanUtils.encodeQRCode("hahah", 1000, 1000);
                imageView.setImageBitmap(bitmap);*/
                break;
            //扫码
            case R.id.scan:
                //开启客户端监听
                new Thread(){
                    public void run(){
                        try {
                            Log.i("haha", ">>>>>>Server start");
                            server = new ServerSocket(9000);
                            Socket socket = null;
                            socket = server.accept();
                            Log.i("haha", ">>>>>>Server accept");
                            //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                            //String str = br.readLine();
                            Log.i("haha", ">>>>>>Server");
                            pw.print("haha");
                            pw.flush();
                            pw.close();
                            socket.close();
                            server.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i("haha", ">>>>>>Server exception");
                        }
                    }
                }.start();


                break;
        }
    }
}
