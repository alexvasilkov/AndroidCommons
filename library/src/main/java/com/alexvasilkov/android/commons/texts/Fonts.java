package com.alexvasilkov.android.commons.texts;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fonts helper. Helps to manage custom fonts in application.
 * <p/>
 * First of all you're need to put your fonts into assets directory and define them as string resources (i.e. in <code>fonts.xml</code>):
 * <pre>
 *     &lt;resources>
 *         &lt;string name="font_arial_regular">fonts/Arial-Regular.ttf&lt;/string>
 *         &lt;string name="font_arial_bold">font:path/to/font/Arial-Bold.ttf&lt;/string>
 *     &lt;/resources>
 * </pre>
 * <p/>
 * Note: this class will only use fonts under <code>fonts/</code> directory and fonts starting with <code>font:</code> preffix.
 * <p/>
 * Now you can use custom fonts in your XML layouts using <code>android:tag</code> attribute:
 * <pre>
 *     &lt;TextView
 *         ...
 *         android:tag="@string/font_arial_regular" />
 * </pre>
 * <p/>
 * And last step you should call one of <code>Fonts.apply(...)</code> methods:
 * <p/>
 * {@link #apply(android.app.Activity)}<br/>
 * {@link #apply(android.view.View)}<br/>
 * {@link #apply(android.widget.TextView, int)}<br/>
 * {@link #apply(android.widget.TextView, String)}
 */
public final class Fonts {

    private static final Pattern FONT_PATTERN = Pattern.compile("^(?:font:)?(fonts/.*|(?<=font:).*)");

    private static Map<String, Typeface> sFontsCacheMap = new HashMap<String, Typeface>();

    /**
     * Applies fonts to all TextView views in Activity's window decor view.<br/>
     * TextView tag will be used to determine font.
     */
    public static void apply(Activity activity) {
        ViewGroup root = (ViewGroup) activity.getWindow().getDecorView();
        if (root.isInEditMode()) return;

        applyAllRecursively(root, activity.getAssets());
    }

    /**
     * If view is instance of ViewGroup, than applies fonts to all TextView views in given ViewGroup<br/>
     * If view is instance of TextView, than applies font to provided TextView<br/>
     * TextView tag will be used to determine font.
     */
    public static void apply(View view) {
        if (view.isInEditMode()) return;

        AssetManager assets = view.getContext().getAssets();
        if (view instanceof TextView) {
            setTypeface((TextView) view, getFontFromTag(assets, view, false));
        } else if (view instanceof ViewGroup) {
            applyAllRecursively((ViewGroup) view, assets);
        }
    }

    /**
     * Applies font to TextView<br/>
     * Note: this class will only accept fonts under <code>fonts/</code> directory and fonts starting with <code>font:</code> preffix.
     */
    public static void apply(TextView textView, int fontStringId) {
        apply(textView, textView.getContext().getString(fontStringId));
    }

    /**
     * Applies font to TextView<br/>
     * Note: this class will only accept fonts under <code>fonts/</code> directory and fonts starting with <code>font:</code> preffix.
     */
    public static void apply(TextView textView, String fontPath) {
        if (textView.isInEditMode()) return;

        setTypeface(textView, getFontFromString(textView.getContext().getAssets(), fontPath, true));
    }


    /* Internal methods */

    private static void applyAllRecursively(ViewGroup viewGroup, AssetManager assets) {
        final int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = viewGroup.getChildAt(i);
            if (childView instanceof TextView) {
                setTypeface((TextView) childView, assets, false);
            } else if (childView instanceof ViewGroup) {
                applyAllRecursively((ViewGroup) childView, assets);
            }
        }
    }

    private static void setTypeface(TextView textView, AssetManager assets, boolean strict) {
        setTypeface(textView, getFontFromTag(assets, textView, strict));
    }

    private static void setTypeface(TextView textView, Typeface typeface) {
        if (typeface == null) return; // To not override previous typeface
        // Enabling sub-pixel rendering
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        textView.setTypeface(typeface);
    }

    static Typeface getTypeface(String fontPath, AssetManager assets) {
        return getFontFromString(assets, fontPath, true);
    }

    /* Helper methods */

    private static Typeface getFontFromTag(AssetManager assets, View view, boolean strict) {
        Object tagObject = view.getTag();
        String tag = tagObject instanceof String ? (String) tagObject : null;
        return getFontFromString(assets, tag, strict);
    }

    private static Typeface getFontFromString(AssetManager assets, String str, boolean strict) {
        Typeface font = sFontsCacheMap.get(str);
        if (font == null) {
            String path = getFontPathFromString(str, strict);
            if (path != null) {
                font = Typeface.createFromAsset(assets, path);
                sFontsCacheMap.put(str, font);
            }
        }
        return font;
    }

    private static String getFontPathFromString(String str, boolean strict) {
        if (str != null) {
            Matcher m = FONT_PATTERN.matcher(str);
            if (m.matches()) return m.group(1);
        }
        if (strict) throw new RuntimeException("Invalid font path: " + str);
        return null;
    }

    private Fonts() {
    }

}
