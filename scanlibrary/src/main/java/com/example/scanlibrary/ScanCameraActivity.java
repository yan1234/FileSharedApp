package com.example.scanlibrary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;


import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ScanCameraActivity extends Activity {

    private static final String TAG = ScanCameraActivity.class.getSimpleName();

    //定义相机对象
    private Camera mCamera = null;
    private CameraPreview mCameraPreview;
    //定义扫码框对象
    private FinderView finderView;

    private PreviewCallback previewCallback = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            Log.i(TAG, "调用相机预览》》》");
            Rect scanImageRect = finderView.getScanImageRect(size.width, size.height);
            //取得指定范围的帧的数据
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                    data,
                    size.width, size.height,
                    scanImageRect.left, scanImageRect.top,
                    scanImageRect.width(),scanImageRect.height()
                    ,false);
            //取得灰度图
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            try {
                Result result = reader.decode(bitmap, hints);
                Log.e(TAG, "结果为：》》》》》》》" + result.getBarcodeFormat()+">>>>>>>"+result.getText());
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_camera);
        //初始化界面
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView(){
        mCameraPreview = (CameraPreview)findViewById(R.id.camera_preview);
        finderView = (FinderView)findViewById(R.id.finder_view);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        mCamera = Camera.open();
        mCameraPreview.setCamera(mCamera);
        mCameraPreview.setPreviewCallback(previewCallback);
    }

    @Override
    public void onResume(){
        super.onResume();
        //初始化事件
        initEvent();
    }
    @Override
    public void onPause(){
        super.onPause();
        releaseCamera();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //释放相机资源
        releaseCamera();
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera(){
        if (mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.autoFocus(null);
            mCameraPreview.setCamera(null);
            mCamera = null;
        }
    }
    /**
     * 关闭闪光灯
     */
    private void closeFlashlight(){
        Camera.Parameters params = mCamera.getParameters();
        //关闭闪光灯
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        //将闪光灯按钮置为关闭状态
    }
}