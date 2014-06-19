package com.alexvasilkov.android.commons.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FillWidthImageView extends ImageView {

    public static final float DEFAULT_EMPTY_ASPECT = 16f / 9f;

    private float mAspect = DEFAULT_EMPTY_ASPECT;
    private boolean mIsEmptyAspectSpecified = false;
    private boolean mIsSkipCurrentLayoutRequest;

    public FillWidthImageView(Context context) {
        super(context);
    }

    public FillWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FillWidthImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void requestLayout() {
        if (!mIsSkipCurrentLayoutRequest) super.requestLayout();
    }

    private int calculateHeight(int w, float aspect) {
        int wPadding = getPaddingLeft() + getPaddingRight();
        int hPadding = getPaddingTop() + getPaddingBottom();

        // Calculating drawable result width and height
        int dW = w - wPadding;
        int dH = Math.round((float) dW / aspect);

        return dH + hPadding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h;

        if (drawable == null) {
            h = calculateHeight(w, mAspect);
        } else {
            int dW = drawable.getIntrinsicWidth();
            int dH = drawable.getIntrinsicHeight();

            if (dW > 0 && dH > 0) {
                h = calculateHeight(w, (float) dW / (float) dH);
            } else {
                h = calculateHeight(w, mAspect);
            }
        }

        setMeasuredDimension(w, h);
    }

    public void setDefaultEmptyAspect() {
        mIsEmptyAspectSpecified = false;
        setEmptyAspectInternal(DEFAULT_EMPTY_ASPECT);
    }

    public void setEmptyAspect(int width, int height) {
        mIsEmptyAspectSpecified = true;
        setEmptyAspectInternal((float) width / (float) height);
    }

    public void setEmptyAspect(float aspect) {
        mIsEmptyAspectSpecified = true;
        setEmptyAspectInternal(aspect);
    }

    private void setEmptyAspectInternal(float aspect) {
        if (aspect <= 0) throw new IllegalArgumentException("Aspect cannot be <= 0");
        mAspect = aspect;
        requestLayout();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm == null) {
            setImageDrawable(null);
        } else {
            BitmapDrawable drawable = new BitmapDrawable(getResources(), bm);
            bm.setDensity(Bitmap.DENSITY_NONE);
            setImageDrawable(drawable);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mIsSkipCurrentLayoutRequest = mIsEmptyAspectSpecified;
        super.setImageDrawable(drawable);
        mIsSkipCurrentLayoutRequest = false;
    }

    @Override
    public void setImageResource(int resId) {
        setImageDrawable(getResources().getDrawable(resId));
    }
}
