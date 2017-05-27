package com.alexvasilkov.android.commons.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Simplified tinting for drawables and views.
 */
@SuppressWarnings({ "unused", "WeakerAccess" }) // Public API
public class Tints {

    public static final Tint PRIMARY = fromAttr("colorPrimary");
    public static final Tint PRIMARY_DARK = fromAttr("colorPrimaryDark");
    public static final Tint ACCENT = fromAttr("colorAccent");

    public static final Tint NORMAL = fromAttr("colorControlNormal");
    public static final Tint ACTIVATED = fromAttr("colorControlActivated");
    public static final Tint DISABLED = withDisabledAlpha(fromAttr("colorControlNormal"));

    private static final int ALPHA_DISABLED = -1;

    private static final int[][] states = new int[][] {
            new int[] { -android.R.attr.state_enabled }, // disabled
            new int[] { android.R.attr.state_pressed }, // pressed
            new int[] { android.R.attr.state_focused }, // focused
            new int[] { android.R.attr.state_selected }, // selected
            new int[] { android.R.attr.state_activated }, // activated
            new int[] { android.R.attr.state_checked }, // checked
            StateSet.WILD_CARD // other
    };

    private static final TypedValue tmpValue = new TypedValue();
    private static final int[] tmpAttrs = new int[1];


    private Tints() {}


    public static Drawable tint(@NonNull Context context,
            @NonNull Drawable drawable, @NonNull Tint tint) {
        return tint(drawable, tint.getColor(context));
    }

    public static Drawable tint(@NonNull Context context,
            @DrawableRes int drawableId, @NonNull Tint tint) {
        return tint(ContextCompat.getDrawable(context, drawableId), tint.getColor(context));
    }

    private static Drawable tint(Drawable drawable, ColorStateList color) {
        Drawable wrapped = DrawableCompat.wrap(drawable);
        wrapped.mutate();
        DrawableCompat.setTintList(wrapped, color);
        return wrapped;
    }


    /**
     * Tints ImageView drawable or TextView compound drawables to given tint color.
     */
    public static void tint(@NonNull View view, @NonNull Tint tint) {
        final ColorStateList color = tint.getColor(view.getContext());

        if (view instanceof ImageView) {
            tint(((ImageView) view).getDrawable(), color);
        } else if (view instanceof TextView) {
            TextView text = (TextView) view;
            Drawable[] comp = text.getCompoundDrawables();
            for (int i = 0; i < comp.length; i++) {
                if (comp[i] != null) {
                    comp[i] = tint(comp[i], color);
                }
            }
            text.setCompoundDrawablesWithIntrinsicBounds(comp[0], comp[1], comp[2], comp[3]);
        } else {
            throw new IllegalArgumentException("Unsupported view type");
        }
    }


    private static Tint fromAttr(@NonNull final String attrName) {
        return new Tint() {
            @Override
            public ColorStateList getColor(Context context) {
                int attrId = context.getResources()
                        .getIdentifier(attrName, "attr", context.getPackageName());
                return getThemeAttrColor(context, attrId);
            }
        };
    }

    public static Tint fromAttr(@AttrRes final int attrId) {
        return new Tint() {
            @Override
            public ColorStateList getColor(Context context) {
                return getThemeAttrColor(context, attrId);
            }
        };
    }

    public static Tint fromColor(@ColorInt final int color) {
        return new Tint() {
            @Override
            public ColorStateList getColor(Context context) {
                return ColorStateList.valueOf(color);
            }
        };
    }

    public static Tint fromColorRes(@ColorRes final int colorId) {
        return new Tint() {
            @Override
            public ColorStateList getColor(Context context) {
                return ContextCompat.getColorStateList(context, colorId);
            }
        };
    }

    public static Tint fromColorList(@NonNull final ColorStateList color) {
        return new Tint() {
            @Override
            public ColorStateList getColor(Context context) {
                return color;
            }
        };
    }

    public static Tint fromDefaultStates() {
        return new Tint() {
            @Override
            public ColorStateList getColor(Context context) {
                final int normal = NORMAL.getDefaultColor(context);
                final int activated = ACTIVATED.getDefaultColor(context);
                final int disabled = DISABLED.getDefaultColor(context);
                final int[] colors = new int[] {
                        disabled, activated, activated, activated, activated, activated, normal
                };
                return new ColorStateList(states, colors);
            }
        };
    }

    public static Tint withAlpha(@NonNull final Tint tint, final int alpha) {
        return new Tint() {
            @Override
            public ColorStateList getColor(Context context) {
                int appliedAlpha = alpha == ALPHA_DISABLED ? getThemeDisabledAlpha(context) : alpha;
                ColorStateList stateColor = tint.getColor(context);

                if (stateColor.isStateful()) {
                    // New ColorStateList object will be created each time calling .withAlpha()
                    return stateColor.withAlpha(appliedAlpha);
                } else {
                    // Created ColorStateList object will be cached
                    int color = (stateColor.getDefaultColor() & 0xFFFFFF) | (appliedAlpha << 24);
                    return ColorStateList.valueOf(color);
                }
            }
        };
    }

    public static Tint withDisabledAlpha(@NonNull Tint tint) {
        return withAlpha(tint, ALPHA_DISABLED);
    }


    private static ColorStateList getThemeAttrColor(Context context, @AttrRes int attr) {
        tmpAttrs[0] = attr;
        TypedArray arr = context.obtainStyledAttributes(null, tmpAttrs);
        ColorStateList color = arr.getColorStateList(0);
        arr.recycle();
        return color;
    }

    private static int getThemeDisabledAlpha(Context context) {
        context.getTheme().resolveAttribute(android.R.attr.disabledAlpha, tmpValue, true);
        return Math.round(0xFF * tmpValue.getFloat());
    }


    public abstract static class Tint {
        /**
         * Returns ColorStateList for tinting.
         */
        public abstract ColorStateList getColor(Context context);

        /**
         * Returns default color from ColorStateList for tinting.
         * Useful if it is known that tinting color is stateless (representing single color).
         */
        public final int getDefaultColor(Context context) {
            return getColor(context).getDefaultColor();
        }
    }

}

