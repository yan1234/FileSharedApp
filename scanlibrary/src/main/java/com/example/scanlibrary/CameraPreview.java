package com.example.scanlibrary;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * 相机预览界面
 * Created by yanling on 2015/10/20.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    public static final String TAG = CameraPreview.class.getSimpleName();

    //定义holder缓冲
    private SurfaceHolder mHolder;
    //定义相机对象
    private Camera mCamera;
    //定义自动聚焦回调
    private AutoFocusCallback autoFocusCallback;
    private Camera.PreviewCallback previewCallback;
    //设置预览大小
    private Camera.Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;

    private Handler handler = new Handler();

    /**
     * 提供界面资源文件xml中引入的构造函数
     * @param context
     * @param attrs
     */
    public CameraPreview(Context context, AttributeSet attrs){
        super(context, attrs);
        //初始化
        init();
    }

    /**
     * 初始化相机
     * @param camera
     */
    public void setCamera(Camera camera){
        //初始化相机
        this.mCamera = camera;
        if (mCamera != null){
            //得到尺寸
            mSupportedPreviewSizes = mCamera.getParameters()
                    .getSupportedPreviewSizes();
            requestLayout();
        }
    }

    /**
     * 设置自动聚焦回调
     * @param cb
     */
    public void setAutoFocusCallback(AutoFocusCallback cb){
        this.autoFocusCallback = cb;
    }

    /**
     * 设置相机预览回调
     * @param cb
     */
    public void setPreviewCallback(Camera.PreviewCallback cb){
        this.previewCallback = cb;
    }

    /**
     * 初始化
     */
    private void init(){
        //初始化holder
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //初始化自动聚焦回调
        autoFocusCallback = new AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                handler.postDelayed(AutoFocusTask, 2000);
            }
        };
        //没隔2s自动聚焦一次
        handler.postDelayed(AutoFocusTask, 2000);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera != null){
            try {
                //设置缓冲
                mCamera.setPreviewDisplay(holder);
                //开始预览
                mCamera.startPreview();
                mCamera.setPreviewCallback(previewCallback);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "surfaceCreated:>>>>>>>"+e.getMessage());
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null){
            return;
        }
        if (mCamera != null){
            //先停止预览
            mCamera.stopPreview();
            //设置预览参数
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            requestLayout();
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            //开始预览
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                mCamera.setPreviewCallback(previewCallback);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "surfaceChanged:>>>>>"+e.getMessage());
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null){
            mCamera.stopPreview();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
                    height);
        }
    }

    /**
     * 得到最佳的预览画面
     * @param sizes，待筛选的大小
     * @param w，宽比例
     * @param h，高比例
     * @return
     */
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private Runnable AutoFocusTask = new Runnable(){
        @Override
        public void run() {
            if (mCamera != null && autoFocusCallback != null){
                mCamera.autoFocus(autoFocusCallback);
            }
        }
    };

}
