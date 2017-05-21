package com.alexvasilkov.android.commons.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class KeyboardHelper {

    private static final int TAG_LISTENER_ID = -10010;

    private KeyboardHelper() {}

    /**
     * Hides keyboard using currently focused view.<br/>
     * Shortcut for {@link #hideSoftKeyboard(android.content.Context, android.view.View)
     * hideSoftKeyboard(activity, activity.getCurrentFocus())}.
     */
    public static void hideSoftKeyboard(Activity activity) {
        hideSoftKeyboard(activity, activity.getCurrentFocus());
    }

    /**
     * Uses given views to hide soft keyboard and to clear current focus.
     *
     * @param context Context
     * @param focusedView Currently focused view
     */
    public static void hideSoftKeyboard(@NonNull Context context, @Nullable View focusedView) {
        if (focusedView == null) {
            return;
        }

        final InputMethodManager manager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        manager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        focusedView.clearFocus();
    }

    /**
     * Shows soft keyboard and requests focus for given view.
     */
    public static void showSoftKeyboard(Context context, View view) {
        if (view == null) {
            return;
        }

        final InputMethodManager manager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        manager.showSoftInput(view, 0);
    }

    /**
     * Registers listener for soft keyboard state changes.<br/>
     * The state is computed based on rootView height changes.
     *
     * @param rootView Should be deepest full screen view, i.e. root of the layout passed to
     * Activity.setContentView(...) or view returned by Fragment.onCreateView(...)
     * @param listener Keyboard state listener
     */
    public static void addKeyboardListener(@NonNull final View rootView,
            @NonNull final OnKeyboardShowListener listener) {

        final OnGlobalLayoutListener layoutListener = new OnGlobalLayoutListener() {
            private boolean isKeyboardShown;
            private int initialHeightsDiff = -1;

            @Override
            public void onGlobalLayout() {
                final Rect frame = new Rect();
                rootView.getWindowVisibleDisplayFrame(frame);

                int heightDiff = rootView.getRootView().getHeight() - (frame.bottom - frame.top);
                if (initialHeightsDiff == -1) {
                    initialHeightsDiff = heightDiff;
                }
                heightDiff -= initialHeightsDiff;

                if (heightDiff > 100) { // If more than 100 pixels, its probably a keyboard...
                    if (!isKeyboardShown) {
                        isKeyboardShown = true;
                        listener.onKeyboardShow(true);
                    }
                } else if (heightDiff < 50) {
                    if (isKeyboardShown) {
                        isKeyboardShown = false;
                        listener.onKeyboardShow(false);
                    }
                }
            }
        };

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        rootView.setTag(TAG_LISTENER_ID, layoutListener);
    }

    @SuppressWarnings("deprecation")
    public static void removeKeyboardListener(@NonNull View rootView) {
        final OnGlobalLayoutListener layoutListener =
                (OnGlobalLayoutListener) rootView.getTag(TAG_LISTENER_ID);
        Views.removeOnGlobalLayoutListener(rootView, layoutListener);
    }


    public interface OnKeyboardShowListener {
        void onKeyboardShow(boolean show);
    }

}
