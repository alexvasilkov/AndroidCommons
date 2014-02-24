package com.azcltd.fluffycommons.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Typeface helper. Helps to manage custom typefaces in application.
 *
 * Strings resource file example (typefaces.xml):
 *
 * <resources>
 *      <string name="typeface_arial_regular">fonts/Arial-Regular.ttf</string>
 * </resources>
 *
 *
 * Layout resource file example:
 *
 * <TextView
 *      android:id="@+id/text_view"
 *      android:layout_width="wrap_content"
 *      android:layout_height="wrap_content"
 *      android:tag="@string/typeface_arial_regular"/>
 *
 *
 * Usage example #1 (setting Activity typefaces, TextViews' XML attribute android:tag is being used to determine typeface):
 *
 * TypefaceHelper.setTypefaces(this);
 *
 *
 * Usage example #2 (setting View typefaces, TextViews' XML attribute android:tag is being used to determine typeface):
 *
 * View view = getWindow().getDecorView();
 * TypefaceHelper.setTypefaces(view);
 *
 *
 * Usage example #3 (setting TextView typeface manually by ID):
 *
 * TextView textView = (TextView) findViewById(R.id.text_view);
 * TypefaceHelper.setTypeface(textView, R.string.typeface_arial_bold);
 */
public final class TypefaceHelper {

    /*
     * Typeface map - mapping typeface path in assets to actual Typeface object
     */
    private static HashMap<String, Typeface> sTypefacesMap = new HashMap<String, Typeface>();

    /**
     * Registers typeface for further use.
     *
     * @param typefaceStringId string resource ID (for example: R.string.typeface_arial_bold)
     * @param context context
     */
    public static void registerTypeface(int typefaceStringId, Context context) {
        checkInMainThread();

        String assetsPath = context.getString(typefaceStringId);
        checkTypefaceNotRegistered(assetsPath);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), assetsPath);

        sTypefacesMap.put(assetsPath, typeface);
    }

    /**
     * Unregisters typeface.
     *
     * @param typefaceStringId string resource ID (for example: R.string.typeface_arial_bold)
     * @param context context
     */
    public static void unregisterTypeface(int typefaceStringId, Context context) {
        checkInMainThread();

        String assetsPath = context.getString(typefaceStringId);
        sTypefacesMap.remove(assetsPath);
    }

    /**
     * Applies typefaces to all TextView views in Activity's window decor view.
     * TextView tag will be used to determine typeface ID.
     *
     * @param activity activity
     */
    public static void setTypefaces(Activity activity) {
        ViewGroup decorViewGroup = (ViewGroup) activity.getWindow().getDecorView();
        setTypefaces(decorViewGroup);
    }

    /**
     * Applies typefaces to all TextView views in specified view.
     * TextView tag will be used to determine typeface ID.
     *
     * @param view view
     */
    public static void setTypefaces(View view) {
        if (view instanceof ViewGroup) {
            setTypefaces((ViewGroup) view);
        } else if (view instanceof TextView) {
            setTypeface((TextView) view);
        }
    }

    /**
     * Applies typeface to TextView manually by using string resource ID.
     *
     * @param textView TextView
     * @param typefaceStringId string resource ID (for example: R.string.typeface_arial_bold)
     */
    public static void setTypeface(TextView textView, int typefaceStringId) {
        String assetsPath = textView.getContext().getString(typefaceStringId);
        setTypeface(textView, assetsPath);
    }

    /*
     * Internal methods
     */

    private static void setTypefaces(ViewGroup viewGroup) {
        final int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = viewGroup.getChildAt(i);
            if (childView instanceof TextView) {
                setTypeface((TextView) childView);
            } else if (childView instanceof ViewGroup) {
                setTypefaces((ViewGroup) childView);
            }
        }
    }

    private static void setTypeface(TextView textView) {
        Object tagObject = textView.getTag();
        if (tagObject != null && tagObject instanceof String) {
            setTypeface(textView, (String) tagObject);
        }
    }

    private static void setTypeface(TextView textView, String typefaceAssetsPath) {
        checkInMainThread();

        if (!sTypefacesMap.containsKey(typefaceAssetsPath)) {
            return;
        }

        if (!textView.isInEditMode()) {
            // Enabling sub-pixel rendering
            textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            textView.setTypeface(sTypefacesMap.get(typefaceAssetsPath));
        }
    }

    /*
     * Helper methods
     */

    private static void checkInMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("This method should be called from the main/UI thread!");
        }
    }

    private static void checkTypefaceNotRegistered(String typefaceAssetsPath) {
        if (sTypefacesMap.containsKey(typefaceAssetsPath)) {
            throw new RuntimeException("Typeface " + typefaceAssetsPath + " already registered!");
        }
    }

    private TypefaceHelper() {}

}
