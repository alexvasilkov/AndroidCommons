package com.alexvasilkov.android.commons.state;

import android.os.Bundle;
import android.os.Parcelable;
import com.alexvasilkov.android.commons.utils.GsonHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helps saving and restoring {@link android.app.Activity} or {@link android.app.Fragment} instance state.<br/>
 * Only local fields marked with {@link InstanceState} annotation will be saved.<br/>
 * Supported fields types: boolean, boolean[], byte, byte[], char, char[], CharSequence, CharSequence[], double,
 * double[], float, float[], int, int[], long, long[], short, short[], String, String[], Bundle and all objects
 * implementing Serializable.<br/>
 * See also {@link #saveInstanceState(Object, android.os.Bundle)} and
 * {@link #restoreInstanceState(Object, android.os.Bundle)} methods.
 */
public class InstanceStateManager<T> {

    public static final String PREFIX = "instance_state:";

    /**
     * Saving instance state of the given {@code obj} into {@code outState}.<br/>
     * Supposed to be called from {@link android.app.Activity#onSaveInstanceState(android.os.Bundle)} or
     * {@link android.app.Fragment#onSaveInstanceState(android.os.Bundle)}.<br/>
     * Activity or Fragment itself can be used as {@code obj} parameter.
     */
    public static <T> Bundle saveInstanceState(T obj, Bundle outState) {
        if (outState == null) outState = new Bundle();

        return new InstanceStateManager<T>(obj).saveState(outState);
    }

    /**
     * Restoring instance state from given {@code savedInstanceState} into the given {@code obj}.<br/>
     * Supposed to be called from {@link android.app.Activity#onCreate(android.os.Bundle)} or
     * {@link android.app.Fragment#onCreate(android.os.Bundle)} prior to using of local fields marked with
     * {@link InstanceState} annotation.
     */
    public static <T> void restoreInstanceState(T obj, Bundle savedInstanceState) {
        if (savedInstanceState == null) return;

        new InstanceStateManager<T>(obj).restoreState(savedInstanceState);
    }

    private T mObj;
    private final HashMap<String, Field> mFieldsMap = new HashMap<String, Field>();
    private final HashMap<String, Boolean> mIsGsonMap = new HashMap<String, Boolean>();
    private final HashMap<Field, String> mKeysMap = new HashMap<Field, String>();

    private InstanceStateManager(T obj) {
        mObj = obj;

        InstanceState an;
        String key;

        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            addFields(clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
    }

    private void addFields(Field[] fields) {
        String key;
        boolean isGson;

        for (Field f : fields) {
            if (f.getAnnotation(InstanceState.class) != null) {
                isGson = false;
            } else if (f.getAnnotation(InstanceStateGson.class) != null) {
                if (!GsonHelper.hasGson())
                    throw new RuntimeException("Gson library not found for InstanceStateGson annotation");
                isGson = true;
            } else {
                continue;
            }
            key = f.getName();

            if (mFieldsMap.containsKey(key)) {
                throw new RuntimeException("Duplicate key \"" + key + "\" of InstanceState annotation");
            } else {
                f.setAccessible(true); // removing private fields access restriction
                mFieldsMap.put(key, f);
                mIsGsonMap.put(key, isGson);
                mKeysMap.put(f, key);
            }
        }
    }

    private Bundle saveState(Bundle outState) {
        try {
            String key;
            for (Field f : mKeysMap.keySet()) {
                key = mKeysMap.get(f);
                setBundleValue(f, mObj, outState, PREFIX + key, mIsGsonMap.get(key));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't access field value", e);
        }
        return outState;
    }

    private void restoreState(Bundle savedInstanceState) {
        try {
            String key;
            for (Field f : mKeysMap.keySet()) {
                key = mKeysMap.get(f);
                setInstanceValue(f, mObj, savedInstanceState, PREFIX + key, mIsGsonMap.get(key));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't set field value", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void setBundleValue(Field f, Object obj, Bundle bundle, String key, boolean isGson)
            throws IllegalAccessException {

        if (isGson) {
            bundle.putString(key, GsonHelper.toJson(f.get(obj)));
            return;
        }

        Class<?> type = f.getType();
        Type[] genericTypes = null;
        if (f.getGenericType() instanceof ParameterizedType) {
            genericTypes = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
        }

        if (type.equals(Boolean.TYPE)) {
            bundle.putBoolean(key, f.getBoolean(obj));

        } else if (type.equals(boolean[].class)) {
            bundle.putBooleanArray(key, (boolean[]) f.get(obj));

        } else if (type.equals(Bundle.class)) {
            bundle.putBundle(key, (Bundle) f.get(obj));

        } else if (type.equals(Byte.TYPE)) {
            bundle.putByte(key, f.getByte(obj));

        } else if (type.equals(byte[].class)) {
            bundle.putByteArray(key, (byte[]) f.get(obj));

        } else if (type.equals(Character.TYPE)) {
            bundle.putChar(key, f.getChar(obj));

        } else if (type.equals(char[].class)) {
            bundle.putCharArray(key, (char[]) f.get(obj));

        } else if (type.equals(CharSequence.class)) {
            bundle.putCharSequence(key, (CharSequence) f.get(obj));

        } else if (type.equals(CharSequence[].class)) {
            bundle.putCharSequenceArray(key, (CharSequence[]) f.get(obj));

        } else if (type.equals(Double.TYPE)) {
            bundle.putDouble(key, f.getDouble(obj));

        } else if (type.equals(double[].class)) {
            bundle.putDoubleArray(key, (double[]) f.get(obj));

        } else if (type.equals(Float.TYPE)) {
            bundle.putFloat(key, f.getFloat(obj));

        } else if (type.equals(float[].class)) {
            bundle.putFloatArray(key, (float[]) f.get(obj));

        } else if (type.equals(Integer.TYPE)) {
            bundle.putInt(key, f.getInt(obj));

        } else if (type.equals(int[].class)) {
            bundle.putIntArray(key, (int[]) f.get(obj));

        } else if (type.equals(Long.TYPE)) {
            bundle.putLong(key, f.getLong(obj));

        } else if (type.equals(long[].class)) {
            bundle.putLongArray(key, (long[]) f.get(obj));

        } else if (type.equals(Short.TYPE)) {
            bundle.putShort(key, f.getShort(obj));

        } else if (type.equals(short[].class)) {
            bundle.putShortArray(key, (short[]) f.get(obj));

        } else if (type.equals(String.class)) {
            bundle.putString(key, (String) f.get(obj));

        } else if (type.equals(String[].class)) {
            bundle.putStringArray(key, (String[]) f.get(obj));

        } else if (Parcelable.class.isAssignableFrom(type)) {
            bundle.putParcelable(key, (Parcelable) f.get(obj));

        } else if (type.equals(ArrayList.class) && genericTypes[0] instanceof Class &&
                Parcelable.class.isAssignableFrom((Class<?>) genericTypes[0])) {
            bundle.putParcelableArrayList(key, (ArrayList<? extends Parcelable>) f.get(obj));

        } else if (type.isArray() && Parcelable.class.isAssignableFrom(type.getComponentType())) {
            bundle.putParcelableArray(key, (Parcelable[]) f.get(obj));

        } else if (Serializable.class.isAssignableFrom(type)) {
            bundle.putSerializable(key, (Serializable) f.get(obj));

        } else {
            throw new RuntimeException("Unsupported field type: " + f.getName() + ", " + type.getName());
        }
    }

    private static void setInstanceValue(Field f, Object obj, Bundle bundle, String key, boolean isGson)
            throws IllegalArgumentException, IllegalAccessException {

        if (isGson) {
            f.set(obj, GsonHelper.fromJson(bundle.getString(key), f.getGenericType()));
            return;
        }

        Class<?> type = f.getType();

        Type[] genericTypes = null;
        if (f.getGenericType() instanceof ParameterizedType) {
            genericTypes = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
        }

        if (type.equals(Boolean.TYPE)) {
            f.setBoolean(obj, bundle.getBoolean(key));

        } else if (type.equals(boolean[].class)) {
            f.set(obj, bundle.getBooleanArray(key));

        } else if (type.equals(Bundle.class)) {
            f.set(obj, bundle.getBundle(key));

        } else if (type.equals(Byte.TYPE)) {
            f.setByte(obj, bundle.getByte(key));

        } else if (type.equals(byte[].class)) {
            f.set(obj, bundle.getByteArray(key));

        } else if (type.equals(Character.TYPE)) {
            f.setChar(obj, bundle.getChar(key));

        } else if (type.equals(char[].class)) {
            f.set(obj, bundle.getCharArray(key));

        } else if (type.equals(CharSequence.class)) {
            f.set(obj, bundle.getCharSequence(key));

        } else if (type.equals(CharSequence[].class)) {
            f.set(obj, bundle.getCharSequenceArray(key));

        } else if (type.equals(Double.TYPE)) {
            f.setDouble(obj, bundle.getDouble(key));

        } else if (type.equals(double[].class)) {
            f.set(obj, bundle.getDoubleArray(key));

        } else if (type.equals(Float.TYPE)) {
            f.setFloat(obj, bundle.getFloat(key));

        } else if (type.equals(float[].class)) {
            f.set(obj, bundle.getFloatArray(key));

        } else if (type.equals(Integer.TYPE)) {
            f.setInt(obj, bundle.getInt(key));

        } else if (type.equals(int[].class)) {
            f.set(obj, bundle.getIntArray(key));

        } else if (type.equals(Long.TYPE)) {
            f.setLong(obj, bundle.getLong(key));

        } else if (type.equals(long[].class)) {
            f.set(obj, bundle.getLongArray(key));

        } else if (type.equals(Short.TYPE)) {
            f.setShort(obj, bundle.getShort(key));

        } else if (type.equals(short[].class)) {
            f.set(obj, bundle.getShortArray(key));

        } else if (type.equals(String.class)) {
            f.set(obj, bundle.getString(key));

        } else if (type.equals(String[].class)) {
            f.set(obj, bundle.getStringArray(key));

        } else if (Parcelable.class.isAssignableFrom(type)) {
            f.set(obj, bundle.getParcelable(key));

        } else if (type.equals(ArrayList.class) && genericTypes[0] instanceof Class &&
                Parcelable.class.isAssignableFrom((Class<?>) genericTypes[0])) {
            f.set(obj, bundle.getParcelableArrayList(key));

        } else if (type.isArray() && Parcelable.class.isAssignableFrom(type.getComponentType())) {
            f.set(obj, bundle.getParcelableArray(key));

        } else if (Serializable.class.isAssignableFrom(type)) {
            f.set(obj, bundle.getSerializable(key));

        } else {
            throw new RuntimeException("Unsupported field type: " + f.getName() + ", " + type.getSimpleName());
        }

        bundle.remove(key);
    }
}