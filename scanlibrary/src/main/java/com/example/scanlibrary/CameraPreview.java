package com.example.scanlibrary;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.jar.Attributes;

/**
 * 扫码相机预览界面
 * @author yanling
 * @date 2015-10-17
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    //定义日志标记
    public static final String TAG = CameraPreview.class.getSimpleName();
    //定义上下文
    private Context mContext;
    //定义相机对象
    private Camera mCamera = null;

    /**
     * 便于资源xml文件中使用该控件
     * @param context
     * @param attrs
     */
    public CameraPreview(Context context, AttributeSet attrs){
        super(context, attrs);
        this.mContext = context;
    }

    public void init(Camera camera, PreviewCallback preViewCB, AutoFocusCallback autoFocusCB){
        this.mCamera = camera;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateCameraParameters();
        mCamera.setPreviewCallback(previewCallback);
        mCamera.startPreview();
        mCamera.autoFocus(autoFocusCallback);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
