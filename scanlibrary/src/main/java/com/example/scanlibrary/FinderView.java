package com.example.scanlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 扫码框设计
 * Created by yanling on 2015/10/20.
 */
public class FinderView extends View{

    public static final String TAG = FinderView.class.getSimpleName();

    //设置移动延时
    private static final long ANIMATION_DELAY = 10;
    //设置移动的距离
    private static final int MOVE_HEIGHT = 30;

    private Context mContext;
    //定义Paint对象
    private Paint finderMaskPaint;
    //定义尺寸宽度
    private int measureedWidth;
    //定义尺寸高度
    private int measureedHeight;

    //上下左右矩阵
    private Rect topRect = new Rect();
    private Rect bottomRect = new Rect();
    private Rect rightRect = new Rect();
    private Rect leftRect = new Rect();
    private Rect middleRect = new Rect();
    private Rect lineRect = new Rect();
    //扫描框、线
    private Drawable scan_kuang;
    private Drawable scan_line;
    private String marker = "扫描";
    private int lineHeight;

    public FinderView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.mContext = context;
        //初始化
        init();
    }

    /**
     * 初始化事件
     */
    private void init(){
        int finder_mask = mContext.getResources().getColor(R.color.cover_color);
        finderMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        finderMaskPaint.setColor(finder_mask);
        scan_kuang = mContext.getResources().getDrawable(R.drawable.scan_kuang);
        scan_line = mContext.getResources().getDrawable(R.drawable.scan_line);
        lineHeight = 10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(leftRect, finderMaskPaint);
        canvas.drawRect(topRect, finderMaskPaint);
        canvas.drawRect(rightRect, finderMaskPaint);
        canvas.drawRect(bottomRect, finderMaskPaint);
        //背景框
        scan_kuang.setBounds(middleRect);
        scan_kuang.draw(canvas);
        if (lineRect.bottom < middleRect.bottom) {
            /**
             * postInvalidateDelayed每次刷新的时候，
             * 只要线的底部小于框的底部，就会进入此方法，
             * lineRect.top和lineRect.bottom值再累加，
             * lineHeight是个固定值10，所以，
             * 在这个地方完成了中间横线的上下移动！！！！
             */
            lineRect.top = lineRect.top + MOVE_HEIGHT / 2;
            lineRect.bottom = lineRect.bottom + MOVE_HEIGHT / 2;
            scan_line.setBounds(lineRect);
        } else {
            lineRect.set(middleRect);
            lineRect.bottom = lineRect.top + MOVE_HEIGHT;
            scan_line.setBounds(lineRect);
        }
        scan_line.draw(canvas);
        //描绘文字
        float size = 20f;
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setTextSize(size);
        Rect rect = new Rect();
        p.getTextBounds(marker, 0, marker.length(), rect);
        //定义绘制的x, y值
        float x = (float) ((getWidth()-(rect.right-rect.left)) / 2.0);
        float y = bottomRect.top + 20f;
        canvas.drawText(marker, x, y, p);
        postInvalidateDelayed(ANIMATION_DELAY, middleRect.left, middleRect.top, middleRect.right, middleRect.bottom);
    }
    /**
     * 获取扫描框的大小
     * @param w
     * @param h
     * @return
     */
    public Rect getScanImageRect(int w, int h) {
        Rect rect = new Rect();
        rect.left = middleRect.left;
        rect.right = middleRect.right;
        float temp = h / (float) measureedHeight;
        rect.top = (int) (middleRect.top * temp);
        rect.bottom = (int) (middleRect.bottom * temp);
        return rect;
    }

    /**
     * 计算控件大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureedWidth = MeasureSpec.getSize(widthMeasureSpec);
        measureedHeight = MeasureSpec.getSize(heightMeasureSpec);
        int borderWidth = measureedWidth / 2 + 100;
        middleRect.set((measureedWidth - borderWidth) / 2,
                (measureedHeight - borderWidth) / 2,
                (measureedWidth - borderWidth) / 2 + borderWidth,
                (measureedHeight - borderWidth) / 2 + borderWidth);
        lineRect.set(middleRect);
        lineRect.bottom = lineRect.top + lineHeight;
        leftRect.set(0, middleRect.top, middleRect.left, middleRect.bottom);
        topRect.set(0, 0, measureedWidth, middleRect.top);
        rightRect.set(middleRect.right, middleRect.top, measureedWidth, middleRect.bottom);
        bottomRect.set(0, middleRect.bottom, measureedWidth, measureedHeight);
    }
}
