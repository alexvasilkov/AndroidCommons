package com.alexvasilkov.android.commons.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressWarnings("unused") // Public API
public class FillWidthImageView extends ImageView {

    public static final float DEFAULT_EMPTY_ASPECT = 16f / 9f;

    private float aspect = DEFAULT_EMPTY_ASPECT;
    private boolean isEmptyAspectSpecified = false;
    private boolean isSkipCurrentLayoutRequest;

    public FillWidthImageView(Context context) {
        super(context);
    }

    public FillWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void requestLayout() {
        if (!isSkipCurrentLayoutRequest) {
            super.requestLayout();
        }
    }

    private int calculateHeight(int w, float aspect) {
        final int wPadding = getPaddingLeft() + getPaddingRight();
        final int hPadding = getPaddingTop() + getPaddingBottom();

        // Calculating drawable result width and height
        final int dW = w - wPadding;
        final int dH = Math.round((float) dW / aspect);

        return dH + hPadding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Drawable drawable = getDrawable();

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height;

        if (drawable == null) {
            height = calculateHeight(width, aspect);
        } else {
            final int dW = drawable.getIntrinsicWidth();
            final int dH = drawable.getIntrinsicHeight();

            if (dW > 0 && dH > 0) {
                height = calculateHeight(width, (float) dW / (float) dH);
            } else {
                height = calculateHeight(width, aspect);
            }
        }

        setMeasuredDimension(width, height);
    }

    public void setDefaultEmptyAspect() {
        isEmptyAspectSpecified = false;
        setEmptyAspectInternal(DEFAULT_EMPTY_ASPECT);
    }

    public void setEmptyAspect(int width, int height) {
        isEmptyAspectSpecified = true;
        setEmptyAspectInternal((float) width / (float) height);
    }

    public void setEmptyAspect(float aspect) {
        isEmptyAspectSpecified = true;
        setEmptyAspectInternal(aspect);
    }

    private void setEmptyAspectInternal(float aspect) {
        if (aspect <= 0) {
            throw new IllegalArgumentException("Aspect cannot be <= 0");
        }
        this.aspect = aspect;
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
        isSkipCurrentLayoutRequest = isEmptyAspectSpecified;
        super.setImageDrawable(drawable);
        isSkipCurrentLayoutRequest = false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setImageResource(int resId) {
        if (Build.VERSION.SDK_INT < 21) {
            setImageDrawable(getResources().getDrawable(resId));
        } else {
            setImageDrawable(getContext().getDrawable(resId));
        }
    }

}
