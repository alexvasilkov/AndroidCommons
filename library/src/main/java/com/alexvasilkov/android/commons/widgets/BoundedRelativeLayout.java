package com.alexvasilkov.android.commons.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * {@link RelativeLayout} implementation that allows setting max width and max height using
 * 'android:maxWidth' and 'android:maxHeight' xml attributes.
 */
@SuppressWarnings("unused") // Public API
public class BoundedRelativeLayout extends RelativeLayout {

    private final BoundedViewHelper boundHelper;

    public BoundedRelativeLayout(Context context) {
        this(context, null);
    }

    public BoundedRelativeLayout(Context context, AttributeSet attrs) {
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
