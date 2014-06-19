package com.alexvasilkov.android.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;

public final class KeyboardHelper {

    /**
     * Hides keyboard using currently focused view.<br/>
     * Shortcat for {@link #hideSoftKeyboard(android.content.Context, android.view.View...) hideSoftKeyboard(activity, activity.getCurrentFocus())}.
     */
    public static void hideSoftKeyboard(Activity activity) {
        hideSoftKeyboard(activity, activity.getCurrentFocus());
    }

    /**
     * Uses given views to hide soft keyboard and to clear current focus.
     *
     * @param context Context
     * @param views   Currently focused views
     */
    public static void hideSoftKeyboard(Context context, View... views) {
        if (views == null) return;
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        for (View currentView : views) {
            if (null == currentView) continue;
            manager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            currentView.clearFocus();
        }
    }

    /**
     * Shows soft keyboard and requests focus for given view.
     */
    public static void showSoftKeyboard(Context context, View view) {
        if (view == null) return;
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        manager.showSoftInput(view, 0);
    }

    /**
     * Registers listener for soft keyboard state changes.<br/>
     * The state is computed based on rootView height changes.<br/>
     * Note: In AndroidManifest corresponding activity should have <code>android:windowSoftInputMode</code>
     * set to <code>adjustResize</code>.
     *
     * @param rootView should be deepest full screen view, i.e. root of the layout passed to
     *                 Activity.setContentView(...) or view returned by Fragment.onCreateView(...)
     * @param listener Keyboard state listener
     */
    public static void addKeyboardShowListener(final View rootView, final OnKeyboardShowListener listener) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            private boolean mIsKeyboardShown;
            private int mInitialHeightsDiff = -1;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
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
