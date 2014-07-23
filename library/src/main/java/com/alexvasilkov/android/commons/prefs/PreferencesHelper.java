package com.alexvasilkov.android.commons.prefs;

import android.content.SharedPreferences;
import android.util.Base64;
import com.alexvasilkov.android.commons.utils.GsonHelper;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Helper methods to store additional types of values to {@link SharedPreferences}
 */
public class PreferencesHelper {

    public static final String DEFAULT_DELIMITER = ",";

    /**
     * Converts and stores double value as long.
     */
    public static SharedPreferences.Editor putDouble(SharedPreferences.Editor editor, String key, double value) {
        editor.putLong(key, Double.doubleToLongBits(value));
        return editor;
    }

    /**
     * Retrieves double value stored as long.
     */
    public static double getDouble(SharedPreferences prefs, String key, double defaultValue) {
        long bits = prefs.getLong(key, Double.doubleToLongBits(defaultValue));
        return Double.longBitsToDouble(bits);
    }

    /**
     * Stores given date as long value. <code>Long.MIN_VALUE</code> is used if date is <code>null</code>
     */
    public static SharedPreferences.Editor putDate(SharedPreferences.Editor editor, String key, Date value) {
        editor.putLong(key, value == null ? Long.MIN_VALUE : value.getTime());
        return editor;
    }

    /**
     * Retrieves date value stored as long.
     */
    public static Date getDate(SharedPreferences prefs, String key) {
        long time = prefs.getLong(key, Long.MIN_VALUE);
        return time == Long.MIN_VALUE ? null : new Date(time);
    }

    /**
     * Stores strings array as single string.
     *
     * @param delimiter Delimiter used for strings concatination.
     */
    public static SharedPreferences.Editor putStringArray(SharedPreferences.Editor editor, String key, String[] values, String delimiter) {
        editor.putString(key, concat(values, delimiter));
        return editor;
    }

    /**
     * Stores strings array as single string. Uses {@literal DEFAULT_DELIMITER} as delimiter.
     */
    public static SharedPreferences.Editor putStringArray(SharedPreferences.Editor editor, String key, String[] values) {
        return putStringArray(editor, key, values, DEFAULT_DELIMITER);
    }

    /**
     * Retrieves strings array stored as single string.
     *
     * @param delimiter Delimiter used to split the string.
     */
    public static String[] getStringArray(SharedPreferences prefs, String key, String delimiter) {
        return split(prefs.getString(key, null), delimiter);
    }

    /**
     * Retrieves strings array stored as single string. Uses {@literal DEFAULT_DELIMITER} as delimiter.
     */
    public static String[] getStringArray(SharedPreferences prefs, String key) {
        return getStringArray(prefs, key, DEFAULT_DELIMITER);
    }

    /**
     * Stores serializable object as BASE_64 encoded string.
     */
    public static SharedPreferences.Editor putSerializable(SharedPreferences.Editor editor, String key, Serializable obj) {
        editor.putString(key, serialize(obj));
        return editor;
    }

    /**
     * Retrieves object stored as json encoded string.
     */
    public static <T> T getJson(SharedPreferences prefs, String key, Class<T> clazz) {
        return getJson(prefs, key, (Type) clazz);
    }

    /**
     * Retrieves object stored as json encoded string.
     */
    public static <T> T getJson(SharedPreferences prefs, String key, Type type) {
        return GsonHelper.fromJson(prefs.getString(key, null), type);
    }

    /**
     * Stores object as json encoded string.
     */
    public static SharedPreferences.Editor putJson(SharedPreferences.Editor editor, String key, Object obj) {
        editor.putString(key, GsonHelper.toJson(obj));
        return editor;
    }

    /**
     * Retrieves serializable object stored as BASE_64 encoded string.
     */
    public static Serializable getSerializable(SharedPreferences prefs, String key) {
        return deserialize(prefs.getString(key, null));
    }

    /**
     * Stores object marked with {@link PreferenceObject} annotation to default {@link SharedPreferences}.<br/>
     * Only fields marked with {@link PreferenceField} annotation will be stored.
     * <p/>
     * Supported fields types: <code>boolean</code>, <code>byte</code>, <code>char</code>, <code>double</code>,
     * <code>float</code>, <code>int</code>, <code>long</code>, <code>short</code>, <code>String</code>,
     * <code>String[]</code>, <code>Date</code>, <code>Serializable</code>
     * <p/>
     * See also {@link com.alexvasilkov.android.commons.prefs.Preferences#prefs()}
     *
     * @param obj Object to be saved, cannot be null
     */
    public static <T> void save(T obj) {
        PreferencesManager.save(obj);
    }

    /**
     * Retrieves object stored with {@link #save(Object)} method.
     */
    public static <T> T get(Class<T> clazz) {
        return PreferencesManager.get(clazz);
    }

    /**
     * Removes object stored with {@link #save(Object)} method.
     */
    public static <T> void remove(Class<T> clazz) {
        PreferencesManager.remove(clazz);
    }

    /**
     * Checks if object of this class was already stored
     */
    public static boolean exists(Class<?> clazz) {
        return PreferencesManager.exists(clazz);
    }

    /* Helper methods */

    private static String concat(String[] values, String delimeter) {
        if (values == null || values.length == 0) return null;
        StringBuilder str = new StringBuilder();
        for (String s : values)
            str.append(s).append(delimeter);
        str.delete(str.length() - delimeter.length(), str.length());
        return str.toString();
    }

    private static String[] split(String value, String delimeter) {
        return value == null ? null : value.split(delimeter);
    }

    private static String serialize(Serializable obj) {
        if (obj == null) return null;
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

    private static Serializable deserialize(String serialized) {
        if (serialized == null) return null;
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(Base64.decode(serialized, Base64.DEFAULT));
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

    private PreferencesHelper() {
    }

}
