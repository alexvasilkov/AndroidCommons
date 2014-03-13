package com.azcltd.fluffycommons.texts;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.azcltd.fluffycommons.utils.Preconditions;

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
 * And last step you should call one of <code>Fonts.setAll(...)</code> methods:
 * <p/>
 * {@link #setAll(android.app.Activity)}<br/>
 * {@link #setAll(android.view.ViewGroup)}<br/>
 * <p/>
 * You can also set fonts manually using <code>Fonts.set(...)</code> methods:
 * <p/>
 * {@link #set(android.widget.TextView, int)}<br/>
 * {@link #set(android.widget.TextView, String)}
 */
public final class Fonts {

    private static final Pattern FONT_PATTERN = Pattern.compile("^(?:font:)?(fonts/.*|(?<=font:).*)");

    private static Map<String, Typeface> sFontsCacheMap = new HashMap<String, Typeface>();

    /**
     * Applies fonts to all TextView views in Activity's window decor view.<br/>
     * TextView tag will be used to determine font.
     */
    public static void setAll(Activity activity) {
        setAllRecursive((ViewGroup) activity.getWindow().getDecorView(), activity.getAssets());
    }

    /**
     * Applies fonts to all TextView views in given ViewGroup.<br/>
     * TextView tag will be used to determine font.
     */
    public static void setAll(ViewGroup viewGroup) {
        setAllRecursive(viewGroup, viewGroup.getContext().getAssets());
    }

    private static void setAllRecursive(ViewGroup viewGroup, AssetManager assets) {
        final int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = viewGroup.getChildAt(i);
            if (childView instanceof TextView) {
                setTypeface((TextView) childView, assets, false);
            } else if (childView instanceof ViewGroup) {
                setAllRecursive((ViewGroup) childView, assets);
            }
        }
    }

    /**
     * Applies font to TextView<br/>
     * Note: this class will only accept fonts under <code>fonts/</code> directory and fonts starting with <code>font:</code> preffix.
     */
    public static void set(TextView textView, int fontStringId) {
        set(textView, textView.getContext().getString(fontStringId));
    }

    /**
     * Applies font to TextView<br/>
     * Note: this class will only accept fonts under <code>fonts/</code> directory and fonts starting with <code>font:</code> preffix.
     */
    public static void set(TextView textView, String fontPath) {
        setTypeface(textView, getFontFromString(textView.getContext().getAssets(), fontPath, true));
    }

    /* Internal methods */

    private static void setTypeface(TextView textView, AssetManager assets, boolean strict) {
        setTypeface(textView, getFontFromTag(assets, textView, strict));
    }

    private static void setTypeface(TextView textView, Typeface typeface) {
        Preconditions.checkInMainThread();

        if (!textView.isInEditMode()) {
            // Enabling sub-pixel rendering
            textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            textView.setTypeface(typeface);
        }
    }

    static Typeface getTypeface(String fontPath, AssetManager assets) {
        return getFontFromString(assets, fontPath, true);
    }

    /* Helper methods */

    private static Typeface getFontFromTag(AssetManager assets, TextView view, boolean strict) {
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
