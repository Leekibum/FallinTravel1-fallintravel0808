package com.probum.fallintravel;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by alfo6-25 on 2017-08-22.
 */

public class MyViewPager extends ViewPager {
    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec=widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
