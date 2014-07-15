package com.alexvasilkov.android.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public final class Views {

    @SuppressWarnings("unchecked")
    public static <T extends View> T find(View parent, int viewId) {
        return (T) parent.findViewById(viewId);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T find(Activity activity, int viewId) {
        return (T) activity.findViewById(viewId);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ViewGroup.LayoutParams> T getParams(View view) {
        return (T) view.getLayoutParams();
    }

    public static ViewGroup.MarginLayoutParams getMarginParams(View view) {
        return getParams(view);
    }

    public static FrameLayout.LayoutParams getFrameParams(View view) {
        return getParams(view);
    }

    public static RelativeLayout.LayoutParams getRelativeParams(View view) {
        return getParams(view);
    }

    public static LinearLayout.LayoutParams getLinearParams(View view) {
        return getParams(view);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T inflate(Context context, int layoutId) {
        return (T) LayoutInflater.from(context).inflate(layoutId, null, false);
    }

    public static <T extends View> T inflate(View root, int layoutId) {
        return inflateInternal(root, layoutId, false);
    }

    public static <T extends View> T inflateAndAttach(View root, int layoutId) {
        return inflateInternal(root, layoutId, true);
    }

    @SuppressWarnings("unchecked")
    private static <T extends View> T inflateInternal(View root, int layoutId, boolean attach) {
        if (root == null) throw new NullPointerException("Root view cannot be null");
        return (T) LayoutInflater.from(root.getContext()).inflate(layoutId, (ViewGroup) root, attach);
    }

    private Views() {
    }

}
