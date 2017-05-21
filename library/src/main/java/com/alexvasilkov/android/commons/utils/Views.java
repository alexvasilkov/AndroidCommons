package com.alexvasilkov.android.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Arrays;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public final class Views {

    public static final int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;

    private Views() {}

    @SuppressWarnings("unchecked")
    public static <T extends View> T find(@NonNull View parent, @IdRes int viewId) {
        return (T) parent.findViewById(viewId);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T find(@NonNull Activity activity, @IdRes int viewId) {
        return (T) activity.findViewById(viewId);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ViewGroup.LayoutParams> T getParams(@NonNull View view) {
        return (T) view.getLayoutParams();
    }

    public static ViewGroup.MarginLayoutParams getMarginParams(@NonNull View view) {
        return getParams(view);
    }

    public static FrameLayout.LayoutParams getFrameParams(@NonNull View view) {
        return getParams(view);
    }

    public static RelativeLayout.LayoutParams getRelativeParams(@NonNull View view) {
        return getParams(view);
    }

    public static LinearLayout.LayoutParams getLinearParams(@NonNull View view) {
        return getParams(view);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T inflate(@NonNull Context context, @LayoutRes int layoutId) {
        return (T) LayoutInflater.from(context).inflate(layoutId, null, false);
    }

    public static <T extends View> T inflate(@NonNull View root, @LayoutRes int layoutId) {
        return inflateInternal(root, layoutId, false);
    }

    public static <T extends View> T inflateAndAttach(@NonNull View root, @LayoutRes int layoutId) {
        return inflateInternal(root, layoutId, true);
    }

    @SuppressWarnings("unchecked")
    private static <T extends View> T inflateInternal(@NonNull View root, @LayoutRes int layoutId,
            boolean attach) {
        return (T) LayoutInflater.from(root.getContext())
                .inflate(layoutId, (ViewGroup) root, attach);
    }


    @SuppressWarnings("deprecation")
    public static void setBackground(@NonNull View view, @Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT < 16) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    public static void setBackgroundKeepPadding(@NonNull View view, @Nullable Drawable drawable) {
        final Rect padding = new Rect(
                view.getPaddingLeft(), view.getPaddingTop(),
                view.getPaddingRight(), view.getPaddingBottom());

        setBackground(view, drawable);

        view.setPadding(padding.left, padding.top, padding.right, padding.bottom);
    }

    @SuppressWarnings("deprecation")
    public static void removeOnGlobalLayoutListener(@NonNull View view,
            @NonNull ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    /**
     * Duplicates drawable state from one view to another.<br/>
     * E.g. when 'from' view get pressed 'to' view will be switched to pressed state as well.<br/>
     * State will be duplicated for both background drawable and to ImageView's drawable
     * (if 'to' view is an instance of ImageView).
     */
    public static void duplicateState(@NonNull final View from, @NonNull final View to) {
        from.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final int[] state = from.getDrawableState();

                Drawable back = to.getBackground();
                if (back != null && !Arrays.equals(state, back.getState())) {
                    back.setState(state);
                    back.invalidateSelf();
                }

                Drawable img = to instanceof ImageView ? ((ImageView) to).getDrawable() : null;
                if (img != null && !Arrays.equals(state, img.getState())) {
                    img.setState(state);
                    img.invalidateSelf();
                }

                return true;
            }
        });
    }

}
