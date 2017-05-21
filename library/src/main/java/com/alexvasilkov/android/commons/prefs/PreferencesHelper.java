package com.alexvasilkov.android.commons.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.alexvasilkov.android.commons.utils.GsonHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Helper methods to store additional types of values to {@link SharedPreferences}.
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class PreferencesHelper {

    public static final String DEFAULT_DELIMITER = ",";

    /**
     * Converts and stores double value as long.
     */
    @NonNull
    public static SharedPreferences.Editor putDouble(@NonNull SharedPreferences.Editor editor,
            @NonNull String key, double value) {
        editor.putLong(key, Double.doubleToLongBits(value));
        return editor;
    }

    /**
     * Retrieves double value stored as long.
     */
    public static double getDouble(@NonNull SharedPreferences prefs,
            @NonNull String key, double defaultValue) {
        long bits = prefs.getLong(key, Double.doubleToLongBits(defaultValue));
        return Double.longBitsToDouble(bits);
    }

    /**
     * Stores given date as long value. {@link Long#MIN_VALUE} is used if date is <code>null</code>.
     */
    @NonNull
    public static SharedPreferences.Editor putDate(@NonNull SharedPreferences.Editor editor,
            @NonNull String key, @Nullable Date value) {
        editor.putLong(key, value == null ? Long.MIN_VALUE : value.getTime());
        return editor;
    }

    /**
     * Retrieves date value stored as long.
     */
    @Nullable
    public static Date getDate(@NonNull SharedPreferences prefs, @NonNull String key) {
        long time = prefs.getLong(key, Long.MIN_VALUE);
        return time == Long.MIN_VALUE ? null : new Date(time);
    }

    /**
     * Stores strings array as single string.
     *
     * @param delimiter Delimiter used for strings concatination.
     */
    @NonNull
    public static SharedPreferences.Editor putStringArray(@NonNull SharedPreferences.Editor editor,
            @NonNull String key, @Nullable String[] values, @NonNull String delimiter) {
        editor.putString(key, concat(values, delimiter));
        return editor;
    }

    /**
     * Stores strings array as single string. Uses {@link #DEFAULT_DELIMITER} as delimiter.
     */
    @NonNull
    public static SharedPreferences.Editor putStringArray(@NonNull SharedPreferences.Editor editor,
            @NonNull String key, @Nullable String[] values) {
        return putStringArray(editor, key, values, DEFAULT_DELIMITER);
    }

    /**
     * Retrieves strings array stored as single string.
     *
     * @param delimiter Delimiter used to split the string.
     */
    @Nullable
    public static String[] getStringArray(@NonNull SharedPreferences prefs,
            @NonNull String key, @NonNull String delimiter) {
        return split(prefs.getString(key, null), delimiter);
    }

    /**
     * Retrieves strings array stored as single string.
     * Uses {@link #DEFAULT_DELIMITER} as delimiter.
     */
    @Nullable
    public static String[] getStringArray(@NonNull SharedPreferences prefs, @NonNull String key) {
        return getStringArray(prefs, key, DEFAULT_DELIMITER);
    }

    /**
     * Stores serializable object as BASE_64 encoded string.
     */
    @NonNull
    public static SharedPreferences.Editor putSerializable(@NonNull SharedPreferences.Editor editor,
            @NonNull String key, @Nullable Serializable obj) {
        editor.putString(key, serialize(obj));
        return editor;
    }

    /**
     * Retrieves serializable object stored as BASE_64 encoded string.
     */
    @Nullable
    public static Serializable getSerializable(@NonNull SharedPreferences prefs,
            @NonNull String key) {
        return deserialize(prefs.getString(key, null));
    }

    /**
     * Stores object as json encoded string.
     * Gson library should be available in classpath.
     */
    @NonNull
    public static SharedPreferences.Editor putJson(@NonNull SharedPreferences.Editor editor,
            @NonNull String key, @Nullable Object obj) {
        editor.putString(key, GsonHelper.toJson(obj));
        return editor;
    }

    /**
     * Retrieves object stored as json encoded string.
     * Gson library should be available in classpath.
     */
    @Nullable
    public static <T> T getJson(@NonNull SharedPreferences prefs,
            @NonNull String key, @NonNull Class<T> clazz) {
        return getJson(prefs, key, (Type) clazz);
    }

    /**
     * Retrieves object stored as json encoded string.
     * Gson library should be available in classpath.
     */
    @Nullable
    public static <T> T getJson(@NonNull SharedPreferences prefs,
            @NonNull String key, @NonNull Type type) {
        return GsonHelper.fromJson(prefs.getString(key, null), type);
    }


    /* Helper methods */

    @Nullable
    private static String concat(@Nullable String[] values, @NonNull String delimeter) {
        if (values == null || values.length == 0) {
            return null;
        }
        final StringBuilder str = new StringBuilder();
        for (String val : values) {
            str.append(val).append(delimeter);
        }
        str.delete(str.length() - delimeter.length(), str.length());
        return str.toString();
    }

    @Nullable
    private static String[] split(@Nullable String value, @NonNull String delimeter) {
        return value == null ? null : value.split(delimeter);
    }

    @Nullable
    private static String serialize(@Nullable Serializable obj) {
        if (obj == null) {
            return null;
        }
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            out.close();
            return Base64.encodeToString(byteOut.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    private static Serializable deserialize(@Nullable String serialized) {
        if (serialized == null) {
            return null;
        }
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(
                    Base64.decode(serialized, Base64.DEFAULT));
            ObjectInputStream in = new ObjectInputStream(byteIn);
            Serializable obj = (Serializable) in.readObject();
            in.close();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PreferencesHelper() {}

}
