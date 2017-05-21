package com.alexvasilkov.android.commons.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * {@link FrameLayout} implementation that allows setting max width and max height using
 * 'android:maxWidth' and 'android:maxHeight' xml attributes.
 */
@SuppressWarnings("unused") // Public API
public class BoundedFrameLayout extends FrameLayout {

    private final BoundedViewHelper boundHelper;

    public BoundedFrameLayout(Context context) {
        this(context, null);
    }

    public BoundedFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        boundHelper = new BoundedViewHelper(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                boundHelper.adjustWidthSpecs(widthMeasureSpec),
                boundHelper.adjustHeightSpecs(heightMeasureSpec));
    }

}
