package com.azcltd.fluffycommons;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;

public final class KeyboardHelper {

    public static void hideSoftKeyboard(Activity activity) {
        hideSoftKeyboard(activity, activity.getCurrentFocus());
    }

    public static void hideSoftKeyboard(Context context, View... views) {
        if (views == null) return;
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        for (View currentView : views) {
            if (null == currentView) continue;
            manager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            currentView.clearFocus();
        }
    }

    public static void showSoftKeyboard(Context context, View view) {
        if (view == null) return;
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        manager.showSoftInput(view, 0);
    }

    public static void addKeyboardShowListener(final View rootView, final OnKeyboardShowListener listener) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            private boolean mIsKeyboardShown;
            private int mInitialHeightsDiff = -1;

            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (mInitialHeightsDiff == -1) {
                    mInitialHeightsDiff = heightDiff;
                }
                heightDiff -= mInitialHeightsDiff;

                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    if (!mIsKeyboardShown) {
                        mIsKeyboardShown = true;
                        listener.onKeyboardShow(true);
                    }
                } else if (heightDiff < 50) {
                    if (mIsKeyboardShown) {
                        mIsKeyboardShown = false;
                        listener.onKeyboardShow(false);
                    }
                }
            }
        });
    }

    public static interface OnKeyboardShowListener {
        void onKeyboardShow(boolean show);
    }

    private KeyboardHelper() {
    }
}
