package com.example.scanlibrary;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

import android.os.Bundle;
import android.util.Log;

import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;


public class ScanCameraActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = ScanCameraActivity.class.getSimpleName();

    //定义相机对象
    private Camera mCamera = null;
    //设置相机预览回调
    private PreviewCallback previewCallback = null;
    //设置自动聚焦回调
    private AutoFocusCallback autoFocusCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_camera);
        mCamera = Camera.open();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化相机资源
        if (mCamera == null){
            mCamera = Camera.open();
        }
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            //释放相机资源
            if (mCamera != null){
                mCamera.release();
            }
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null){
            return;
        }
        //停止预览
        mCamera.stopPreview();
        //设置预览参数
        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            //设置预览回调
            mCamera.setPreviewCallback(previewCallback);
            //设置相机预览参数
            setCameraParameters();
            //开始预览
            mCamera.startPreview();
            //设置自动对焦回调
            mCamera.autoFocus(autoFocusCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //关闭闪光灯
        closeFlashlight();
        //释放相机资源
        releaseCamera();
    }

    /**
     * 设置相机参数
     */
    private void setCameraParameters(){
        if (mCamera != null){
            Parameters p = mCamera.getParameters();
            Size picSize = p.getPictureSize();
            Size previewSize = getOptionalPreviewSize(p.getSupportedPreviewSizes(), (double) picSize.width / picSize.height);
            if(previewSize != null){
                p.setPreviewSize(previewSize.width,previewSize.height);
                mCamera.setParameters(p);
            }
        }
    }

    /**
     * 设置合适的预览参数
     * @param sizes， 参数列表
     * @param targetRatio，比对标准
     * @return
     */
    private Size getOptionalPreviewSize(List<Size> sizes, double targetRatio){
        if (sizes == null){
            return null;
        }
        Size optionalSize = null;
        for (Size size : sizes) {
            if (size.width > 700 && size.width < 100) {
                optionalSize = size;
                break;
            }
        }
        return optionalSize;
    }
    /**
     * 释放相机资源
     */
    private void releaseCamera(){
        if (mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.release();
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
