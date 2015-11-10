package com.example.scanlibrary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;


import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.MultiFormatOneDReader;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class ScanCameraActivity extends Activity {

    private static final String TAG = ScanCameraActivity.class.getSimpleName();

    //定义扫码成功后Intent返回的resultCode标志
    public static final int SCAN_OK = 100;
    public static final int SCAN_CANCEL = -100;

    //定义扫码结果的key值变量
    public static final String SCAN_CODE = "SCAN_CODE";


    private CameraPreview mCameraPreview;
    //定义扫码框对象
    private FinderView finderView;
    //定义闪光灯按钮
    private ImageView img_light;
    //定义变量保存闪光灯开启/关闭的状态
    private boolean isFlashlightOn = false;
    //定义解析线程
    private DecodeThread decodeThread;

    private PreviewCallback previewCallback = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            //设置预览数据
            decodeThread.setDecodeData(data, size.width, size.height);
        }
    };

    /**
     * 运用线程去解析相机预览的数据
     */
    class DecodeThread extends Thread{

        //定义待解析的数据
        private byte[] data;
        private int width;
        private int height;

        /**
         * 设置要解析的数据
         */
        public synchronized void setDecodeData(byte[] buff, int width, int height){

            // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
            data = new byte[buff.length];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++)
                    data[x * height + height - y - 1] = buff[x + y * width];
            }
            //置换宽，高
            this.width = height;
            this.height = width;
        }
        @Override
        public void run(){
            while (true){
                //判断数据是否为空
                if (data != null && width >0 && height >0){
                    //解析扫描的信息
                    String result = ScanUtils.decode(data, width, height);
                    //判断是否解析成功
                    if (null != result){
                        //封装解析参数
                        Message msg = new Message();
                        msg.what = SCAN_OK;
                        msg.obj = result;
                        //发送回调消息
                        handler.sendMessage(msg);
                        //结束循环
                        break;
                    }
                    try {
                        //休眠10ms
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 扫码成功handler回调
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SCAN_OK){
                //封装扫码结果
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(SCAN_CODE, (String)msg.obj);
                intent.putExtras(bundle);
                setResult(SCAN_OK, intent);
                ScanCameraActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_camera);
        //开启解析线程
        decodeThread = new DecodeThread();
        decodeThread.start();
    }

    /**
     * 初始化界面
     */
    private void initView(){
        mCameraPreview = (CameraPreview)findViewById(R.id.camera_preview);
        finderView = (FinderView)findViewById(R.id.finder_view);
        img_light = (ImageView)findViewById(R.id.flashlight);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        mCameraPreview.setPreviewCallback(previewCallback);
        img_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //表示闪光灯处于开启状态
                if (isFlashlightOn) {
                    //设置图片为关闭状态
                    img_light.setImageResource(R.drawable.scan_openlight);
                } else {
                    //设置图片为开启状态
                    img_light.setImageResource(R.drawable.scan_closelight);
                }
                //开启/关闭闪光灯
                mCameraPreview.setFlashlightStatus(!isFlashlightOn);
                //改变标志变量
                isFlashlightOn = !isFlashlightOn;
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        //初始化界面
        initView();
        //初始化事件
        initEvent();
    }
    @Override
    public void onPause(){
        super.onPause();
        //释放相机资源
        mCameraPreview.releaseCamera();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    /**
     * 这里重写onKeyDown函数，监听扫码界面的返回按钮，避免setResult的值为空
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断是否是按钮按钮
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            //返回空串
            bundle.putString(SCAN_CODE, "");
            intent.putExtras(bundle);
            setResult(SCAN_CANCEL, intent);
            ScanCameraActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
