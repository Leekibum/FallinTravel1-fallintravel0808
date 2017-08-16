package com.probum.fallintravel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by alfo6-25 on 2017-08-09.
 */

public class MyTextView extends android.support.v7.widget.AppCompatTextView {


    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("TextViewSpec",""+widthMeasureSpec+"   "+heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
