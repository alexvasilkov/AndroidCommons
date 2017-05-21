package com.alexvasilkov.android.commons.texts;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.MetricAffectingSpan;
import android.util.TypedValue;
import android.view.View;

/**
 * SpannableStringBuilder wrapper that allows applying various text styles to single TextView.
 * <p/>
 * Usage example:<br/>
 * <pre>
 * CharSequence text = new SpannableBuilder(this)
 *   .createStyle().setFont("fonts/blessed-day.otf").setColor(Color.RED).setSize(25).apply()
 *   .append("Part1 ")
 *   .currentStyle().setColor(Color.GREEN).setUnderline(true).apply()
 *   .append("Part2 ")
 *   .createStyle().setColor(Color.BLUE).apply()
 *   .append("Part3").build();
 *
 * textView.setText(text);
 * </pre>
 * <p/>
 * In this example: Part1 will use custom font, red color and 25sp font size;
 * Part2 will use custom font, green color, 25sp font size and underline;
 * Part3 will use blue color only.
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class SpannableBuilder {

    private final SpannableStringBuilder builder = new SpannableStringBuilder();

    private final Context context;
    private final OnSpanClickListener clickListener;

    private Style currentStyle;

    public SpannableBuilder(Context context) {
        this(context, null);
    }

    public SpannableBuilder(Context context, OnSpanClickListener clickListener) {
        this.context = context.getApplicationContext();
        this.clickListener = clickListener;
    }

    public Style currentStyle() {
        return currentStyle;
    }

    @NonNull
    public Style createStyle() {
        return new Style(context, this);
    }

    @NonNull
    public SpannableBuilder clearStyle() {
        currentStyle = null;
        return this;
    }

    @NonNull
    public SpannableBuilder append(@StringRes int stringId) {
        return append(stringId, null);
    }

    @NonNull
    public SpannableBuilder append(@StringRes int stringId, Object clickObject) {
        return append(context.getString(stringId), clickObject);
    }

    @NonNull
    public SpannableBuilder append(CharSequence text) {
        return append(text, null);
    }

    @NonNull
    public SpannableBuilder append(CharSequence str, final Object clickObject) {
        if (str == null || str.length() == 0) {
            return this;
        }

        int length = builder.length();
        builder.append(str);

        if (clickObject != null && clickListener != null) {
            ClickableSpan clickSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    clickListener.onSpanClicked(clickObject);
                }
            };
            builder.setSpan(clickSpan, length, length + str.length(),
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        if (currentStyle != null) {
            Span span = new Span(currentStyle);
            builder.setSpan(span, length, length + str.length(),
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return this;
    }

    @NonNull
    public CharSequence build() {
        return builder;
    }


    private static class Span extends MetricAffectingSpan {

        private final Style style;

        public Span(Style style) {
            this.style = style.copy();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            apply(ds);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            apply(paint);
        }

        private void apply(Paint paint) {
            if (style.typeface != null) {
                paint.setTypeface(style.typeface);
            }
            if (style.color != Style.NO_COLOR) {
                paint.setColor(style.color);
            }
            if (style.size != Style.NO_SIZE) {
                paint.setTextSize(style.size);
            }
            paint.setUnderlineText(style.underline);
        }

    }

    public static class Style {

        private static final int NO_COLOR = Integer.MIN_VALUE;
        private static final float NO_SIZE = Float.MIN_VALUE;

        private final Context context; // Note: will be null for copied object
        private final SpannableBuilder parent; // Note: will be null for copied object

        private Typeface typeface;
        private int color = NO_COLOR;
        private float size = NO_SIZE;
        private boolean underline;

        Style(Context context, SpannableBuilder parent) {
            this.context = context;
            this.parent = parent;
        }

        @NonNull
        public Style setFont(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        /**
         * For more details see {@link Fonts}.
         */
        @NonNull
        public Style setFont(String fontPath) {
            return setFont(Fonts.getTypeface(fontPath, context.getAssets()));
        }

        /**
         * For more details see {@link Fonts}.
         */
        @NonNull
        public Style setFont(@StringRes int fontStringId) {
            return setFont(context.getString(fontStringId));
        }

        @NonNull
        public Style setColor(@ColorInt int color) {
            this.color = color;
            return this;
        }

        @SuppressWarnings("deprecation")
        @NonNull
        public Style setColorResId(@ColorRes int colorResId) {
            if (Build.VERSION.SDK_INT < 23) {
                setColor(context.getResources().getColor(colorResId));
            } else {
                setColor(context.getColor(colorResId));
            }
            return this;
        }

        /**
         * @param unit Unit for the value, use COMPLEX_UNIT_* constants from {@link TypedValue}.
         * @param value Value in given unit
         */
        @NonNull
        public Style setSize(int unit, float value) {
            size = TypedValue.applyDimension(unit, value,
                    context.getResources().getDisplayMetrics());
            return this;
        }

        /**
         * Setting size as scaled pixels (SP).
         */
        @NonNull
        public Style setSize(float value) {
            return setSize(TypedValue.COMPLEX_UNIT_SP, value);
        }

        @NonNull
        public Style setUnderline(boolean underline) {
            this.underline = underline;
            return this;
        }

        @NonNull
        public SpannableBuilder apply() {
            parent.currentStyle = this;
            return parent;
        }

        Style copy() {
            Style style = new Style(null, null);
            style.typeface = this.typeface;
            style.color = this.color;
            style.size = this.size;
            style.underline = this.underline;
            return style;
        }

    }

    public interface OnSpanClickListener {

        void onSpanClicked(Object clickObject);

    }

}
