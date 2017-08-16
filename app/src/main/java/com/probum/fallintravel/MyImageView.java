package com.probum.fallintravel;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by alfo6-25 on 2017-08-08.
 */

public class MyImageView extends android.support.v7.widget.AppCompatImageView {
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { //모든 View에 있는 메소드
        heightMeasureSpec=widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
