package com.alexvasilkov.android.commons.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

/**
 * Helper class to allow setting max width and max height to any views using
 * 'android:maxWidth' and 'android:maxHeight' xml attributes.
 */
@SuppressWarnings("WeakerAccess") // Public API
public class BoundedViewHelper {

    private final int maxWidth;
    private final int maxHeight;

    public BoundedViewHelper(Context context, AttributeSet attrs) {
        if (attrs == null) {
            maxWidth = maxHeight = -1;
        } else {
            int[] attrsInts = new int[] { android.R.attr.maxWidth, android.R.attr.maxHeight };
            TypedArray arr = context.obtainStyledAttributes(attrs, attrsInts);
            maxWidth = arr.getDimensionPixelSize(0, -1);
            maxHeight = arr.getDimensionPixelSize(1, -1);
            arr.recycle();
        }
    }

    public int adjustWidthSpecs(int widthMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);

        if (maxWidth > 0 && maxWidth < width) {
            int measureMode = View.MeasureSpec.getMode(widthMeasureSpec);
            return View.MeasureSpec.makeMeasureSpec(maxWidth, measureMode);
        } else {
            return widthMeasureSpec;
        }
    }

    public int adjustHeightSpecs(int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(heightMeasureSpec);

        if (maxHeight > 0 && maxHeight < width) {
            int measureMode = View.MeasureSpec.getMode(heightMeasureSpec);
            return View.MeasureSpec.makeMeasureSpec(maxHeight, measureMode);
        } else {
            return heightMeasureSpec;
        }
    }

}
