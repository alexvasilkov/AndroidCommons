package com.alexvasilkov.android.commons.texts;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.MetricAffectingSpan;
import android.util.TypedValue;
import android.view.View;
import com.alexvasilkov.android.commons.utils.AppContext;

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
public class SpannableBuilder {

    private final SpannableStringBuilder mBuilder = new SpannableStringBuilder();

    private final Context mAppContext;
    private final OnSpanClickListener mClickListener;

    private Style mCurrentStyle;

    public SpannableBuilder() {
        this(AppContext.get());
    }

    public SpannableBuilder(Context appContext) {
        this(appContext, null);
    }

    public SpannableBuilder(Context appContext, OnSpanClickListener clickListener) {
        mAppContext = appContext.getApplicationContext();
        mClickListener = clickListener;
    }

    public Style currentStyle() {
        return mCurrentStyle;
    }

    public Style createStyle() {
        return new Style(mAppContext, this);
    }

    public SpannableBuilder clearStyle() {
        mCurrentStyle = null;
        return this;
    }

    public SpannableBuilder append(int stringId) {
        return append(stringId, null);
    }

    public SpannableBuilder append(int stringId, Object clickObject) {
        return append(mAppContext.getString(stringId), clickObject);
    }

    public SpannableBuilder append(CharSequence text) {
        return append(text, null);
    }

    public SpannableBuilder append(CharSequence str, final Object clickObject) {
        if (str == null || str.length() == 0) return this;

        int length = mBuilder.length();
        mBuilder.append(str);

        if (clickObject != null && mClickListener != null) {
            ClickableSpan clickSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    mClickListener.onSpanClicked(clickObject);
                }
            };
            mBuilder.setSpan(clickSpan, length, length + str.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        if (mCurrentStyle != null) {
            Span span = new Span(mCurrentStyle);
            mBuilder.setSpan(span, length, length + str.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return this;
    }

    public CharSequence build() {
        return mBuilder;
    }

    private static class Span extends MetricAffectingSpan {

        private final Style mStyle;

        public Span(Style style) {
            mStyle = style.clone();
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
            if (mStyle.typeface != null) paint.setTypeface(mStyle.typeface);
            if (mStyle.color != Style.NO_COLOR) paint.setColor(mStyle.color);
            if (mStyle.size != Style.NO_SIZE) paint.setTextSize(mStyle.size);
            paint.setUnderlineText(mStyle.underline);
        }

    }

    public static class Style implements Cloneable {

        private static final int NO_COLOR = Integer.MIN_VALUE;
        private static final float NO_SIZE = Float.MIN_VALUE;

        // Note: will be null for cloned object
        private final Context context;
        private final SpannableBuilder parent;

        private Typeface typeface;
        private int color = NO_COLOR;
        private float size = NO_SIZE;
        private boolean underline;

        private Style(Context context, SpannableBuilder builer) {
            this.context = context;
            this.parent = builer;
        }

        public Style setFont(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        /**
         * For more details see {@link com.alexvasilkov.android.commons.texts.Fonts}
         */
        public Style setFont(String fontPath) {
            return setFont(Fonts.getTypeface(fontPath, context.getAssets()));
        }

        /**
         * For more details see {@link com.alexvasilkov.android.commons.texts.Fonts}
         */
        public Style setFont(int fontStringId) {
            return setFont(context.getString(fontStringId));
        }

        public Style setColor(int color) {
            this.color = color;
            return this;
        }

        public Style setColorResId(int colorResId) {
            return setColor(context.getResources().getColor(colorResId));
        }

        public Style setSize(int unit, float value) {
            size = TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
            return this;
        }

        /**
         * Setting size as scaled pixels (SP)
         */
        public Style setSize(float value) {
            return setSize(TypedValue.COMPLEX_UNIT_SP, value);
        }

        public Style setUnderline(boolean underline) {
            this.underline = underline;
            return this;
        }

        public SpannableBuilder apply() {
            parent.mCurrentStyle = this;
            return parent;
        }

        @Override
        protected Style clone() {
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
