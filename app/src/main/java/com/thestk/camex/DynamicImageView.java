package com.thestk.camex;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Neba on 28-Nov-17.
 */

public class DynamicImageView extends AppCompatImageView {

    private float aspectRatio = 2f;

    public DynamicImageView(Context context) {
        super(context);
    }

    public DynamicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(float value) {
        this.aspectRatio = value;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int newHeight = (int) (MeasureSpec.getSize(widthMeasureSpec) / aspectRatio);
        int value = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, value);
    }
}
