package com.example.filesharedapp.framework.ui.gridview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义图片item的gridview，主要是为了解决两个拥有scrollView 的控件不能嵌套的问题
 * Created by yanling on 2015/11/2.
 */
public class PhotoGridView extends GridView{

    public PhotoGridView(Context context) {
        super(context);
    }

    public PhotoGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置它为不滚动，让gridview显示完全
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
