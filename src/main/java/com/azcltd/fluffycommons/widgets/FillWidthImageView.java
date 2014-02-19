package com.azcltd.fluffycommons.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FillWidthImageView extends ImageView {

    private static final float DEFAULT_EMPTY_ASPECT = 16f / 9f;

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        int w = MeasureSpec.getSize(widthMeasureSpec);

        if (drawable == null) {
            setMeasuredDimension(w, Math.round(w / mAspect));
        } else if (drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int h = w * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
            setMeasuredDimension(w, h);
        }
    }

    public void setDefaultEmptyAspect() {
        mIsEmptyAspectSpecified = false;
        setEmptyAspectInternal(DEFAULT_EMPTY_ASPECT);
    }

    public void setEmptyAspect(int width, int height) {
        mIsEmptyAspectSpecified = true;
        setEmptyAspectInternal((float) width / (float) height);
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
