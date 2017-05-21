package com.alexvasilkov.android.commons.state;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alexvasilkov.android.commons.utils.GsonHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helps saving and restoring {@link android.app.Activity} or {@link android.app.Fragment}
 * instance state.<br/>
 * Only local fields marked with {@link InstanceState} annotation will be saved.<br/>
 * Supported fields types: boolean, boolean[], byte, byte[], char, char[], CharSequence,
 * CharSequence[], double, double[], float, float[], int, int[], long, long[], short, short[],
 * String, String[], Bundle and all objects implementing Serializable.<br/>
 * See also {@link #saveInstanceState(Object, android.os.Bundle)} and
 * {@link #restoreInstanceState(Object, android.os.Bundle)} methods.
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class InstanceStateManager<T> {

    private static final String PREFIX = "instance_state:";

    private T obj;
    private final HashMap<String, Field> fieldsMap = new HashMap<>();
    private final HashMap<String, Boolean> isGsonMap = new HashMap<>();
    private final HashMap<Field, String> keysMap = new HashMap<>();


    /**
     * Saving instance state of the given {@code obj} into {@code outState}.<br/>
     * Supposed to be called from
     * {@link android.app.Activity#onSaveInstanceState(android.os.Bundle)} or
     * {@link android.app.Fragment#onSaveInstanceState(android.os.Bundle)}.<br/>
     * Activity or Fragment itself can be used as {@code obj} parameter.
     */
    @NonNull
    public static <T> Bundle saveInstanceState(@NonNull T obj, @Nullable Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        return new InstanceStateManager<>(obj).saveState(outState);
    }

    /**
     * Restoring instance state from given {@code savedState} into the given {@code obj}.
     * <br/>
     * Supposed to be called from {@link android.app.Activity#onCreate(android.os.Bundle)} or
     * {@link android.app.Fragment#onCreate(android.os.Bundle)} before starting using local fields
     * marked with {@link InstanceState} annotation.
     */
    public static <T> void restoreInstanceState(@NonNull T obj, @Nullable Bundle savedState) {
        if (savedState != null) {
            new InstanceStateManager<>(obj).restoreState(savedState);
        }
    }

    private InstanceStateManager(@NonNull T obj) {
        this.obj = obj;

        InstanceState an;
        String key;

        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            addFields(clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
    }

    private void addFields(@NonNull Field[] fields) {
        String key;
        boolean isGson;

        for (Field field : fields) {
            if (field.getAnnotation(InstanceState.class) != null) {
                isGson = false;
            } else if (field.getAnnotation(InstanceStateGson.class) != null) {
                if (!GsonHelper.hasGson()) {
                    throw new RuntimeException("Gson library not found for InstanceStateGson");
                }
                isGson = true;
            } else {
                continue;
            }
            key = field.getName();

            if (fieldsMap.containsKey(key)) {
                throw new RuntimeException("Duplicate key \"" + key + "\" of InstanceState");
            } else {
                field.setAccessible(true); // Removing private fields access restriction
                fieldsMap.put(key, field);
                isGsonMap.put(key, isGson);
                keysMap.put(field, key);
            }
        }
    }

    @NonNull
    private Bundle saveState(@NonNull Bundle outState) {
        try {
            String key;
            for (Field field : keysMap.keySet()) {
                key = keysMap.get(field);
                setBundleValue(field, obj, outState, PREFIX + key, isGsonMap.get(key));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't access field value", e);
        }
        return outState;
    }

    private void restoreState(@NonNull Bundle savedInstanceState) {
        try {
            String key;
            for (Field field : keysMap.keySet()) {
                key = keysMap.get(field);
                setInstanceValue(field, obj, savedInstanceState, PREFIX + key, isGsonMap.get(key));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't set field value", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void setBundleValue(@NonNull Field field, @NonNull Object obj,
            @NonNull Bundle bundle, @NonNull String key, boolean isGson)
            throws IllegalAccessException {

        if (isGson) {
            bundle.putString(key, GsonHelper.toJson(field.get(obj)));
            return;
        }

        Class<?> type = field.getType();
        Type[] genericTypes = null;
        if (field.getGenericType() instanceof ParameterizedType) {
            genericTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        }

        if (type.equals(Boolean.TYPE)) {
            bundle.putBoolean(key, field.getBoolean(obj));

        } else if (type.equals(boolean[].class)) {
            bundle.putBooleanArray(key, (boolean[]) field.get(obj));

        } else if (type.equals(Bundle.class)) {
            bundle.putBundle(key, (Bundle) field.get(obj));

        } else if (type.equals(Byte.TYPE)) {
            bundle.putByte(key, field.getByte(obj));

        } else if (type.equals(byte[].class)) {
            bundle.putByteArray(key, (byte[]) field.get(obj));

        } else if (type.equals(Character.TYPE)) {
            bundle.putChar(key, field.getChar(obj));

        } else if (type.equals(char[].class)) {
            bundle.putCharArray(key, (char[]) field.get(obj));

        } else if (type.equals(CharSequence.class)) {
            bundle.putCharSequence(key, (CharSequence) field.get(obj));

        } else if (type.equals(CharSequence[].class)) {
            bundle.putCharSequenceArray(key, (CharSequence[]) field.get(obj));

        } else if (type.equals(Double.TYPE)) {
            bundle.putDouble(key, field.getDouble(obj));

        } else if (type.equals(double[].class)) {
            bundle.putDoubleArray(key, (double[]) field.get(obj));

        } else if (type.equals(Float.TYPE)) {
            bundle.putFloat(key, field.getFloat(obj));

        } else if (type.equals(float[].class)) {
            bundle.putFloatArray(key, (float[]) field.get(obj));

        } else if (type.equals(Integer.TYPE)) {
            bundle.putInt(key, field.getInt(obj));

        } else if (type.equals(int[].class)) {
            bundle.putIntArray(key, (int[]) field.get(obj));

        } else if (type.equals(Long.TYPE)) {
            bundle.putLong(key, field.getLong(obj));

        } else if (type.equals(long[].class)) {
            bundle.putLongArray(key, (long[]) field.get(obj));

        } else if (type.equals(Short.TYPE)) {
            bundle.putShort(key, field.getShort(obj));

        } else if (type.equals(short[].class)) {
            bundle.putShortArray(key, (short[]) field.get(obj));

        } else if (type.equals(String.class)) {
            bundle.putString(key, (String) field.get(obj));

        } else if (type.equals(String[].class)) {
            bundle.putStringArray(key, (String[]) field.get(obj));

        } else if (Parcelable.class.isAssignableFrom(type)) {
            bundle.putParcelable(key, (Parcelable) field.get(obj));

        } else if (type.equals(ArrayList.class)
                && genericTypes != null
                && genericTypes[0] instanceof Class
                && Parcelable.class.isAssignableFrom((Class<?>) genericTypes[0])) {
            bundle.putParcelableArrayList(key, (ArrayList<? extends Parcelable>) field.get(obj));

        } else if (type.isArray() && Parcelable.class.isAssignableFrom(type.getComponentType())) {
            bundle.putParcelableArray(key, (Parcelable[]) field.get(obj));

        } else if (Serializable.class.isAssignableFrom(type)) {
            bundle.putSerializable(key, (Serializable) field.get(obj));

        } else {
            throw new RuntimeException("Unsupported field type: " + field.getName()
                    + ", " + type.getName());
        }
    }

    private static void setInstanceValue(@NonNull Field field, @NonNull Object obj,
            @NonNull Bundle bundle, @NonNull String key, boolean isGson)
            throws IllegalArgumentException, IllegalAccessException {

        if (isGson) {
            field.set(obj, GsonHelper.fromJson(bundle.getString(key), field.getGenericType()));
            return;
        }

        Class<?> type = field.getType();

        Type[] genericTypes = null;
        if (field.getGenericType() instanceof ParameterizedType) {
            genericTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        }

        if (type.equals(Boolean.TYPE)) {
            field.setBoolean(obj, bundle.getBoolean(key));

        } else if (type.equals(boolean[].class)) {
            field.set(obj, bundle.getBooleanArray(key));

        } else if (type.equals(Bundle.class)) {
            field.set(obj, bundle.getBundle(key));

        } else if (type.equals(Byte.TYPE)) {
            field.setByte(obj, bundle.getByte(key));

        } else if (type.equals(byte[].class)) {
            field.set(obj, bundle.getByteArray(key));

        } else if (type.equals(Character.TYPE)) {
            field.setChar(obj, bundle.getChar(key));

        } else if (type.equals(char[].class)) {
            field.set(obj, bundle.getCharArray(key));

        } else if (type.equals(CharSequence.class)) {
            field.set(obj, bundle.getCharSequence(key));

        } else if (type.equals(CharSequence[].class)) {
            field.set(obj, bundle.getCharSequenceArray(key));

        } else if (type.equals(Double.TYPE)) {
            field.setDouble(obj, bundle.getDouble(key));

        } else if (type.equals(double[].class)) {
            field.set(obj, bundle.getDoubleArray(key));

        } else if (type.equals(Float.TYPE)) {
            field.setFloat(obj, bundle.getFloat(key));

        } else if (type.equals(float[].class)) {
            field.set(obj, bundle.getFloatArray(key));

        } else if (type.equals(Integer.TYPE)) {
            field.setInt(obj, bundle.getInt(key));

        } else if (type.equals(int[].class)) {
            field.set(obj, bundle.getIntArray(key));

        } else if (type.equals(Long.TYPE)) {
            field.setLong(obj, bundle.getLong(key));

        } else if (type.equals(long[].class)) {
            field.set(obj, bundle.getLongArray(key));

        } else if (type.equals(Short.TYPE)) {
            field.setShort(obj, bundle.getShort(key));

        } else if (type.equals(short[].class)) {
            field.set(obj, bundle.getShortArray(key));

        } else if (type.equals(String.class)) {
            field.set(obj, bundle.getString(key));

        } else if (type.equals(String[].class)) {
            field.set(obj, bundle.getStringArray(key));

        } else if (Parcelable.class.isAssignableFrom(type)) {
            field.set(obj, bundle.getParcelable(key));

        } else if (type.equals(ArrayList.class)
                && genericTypes != null
                && genericTypes[0] instanceof Class
                && Parcelable.class.isAssignableFrom((Class<?>) genericTypes[0])) {
            field.set(obj, bundle.getParcelableArrayList(key));

        } else if (type.isArray() && Parcelable.class.isAssignableFrom(type.getComponentType())) {
            field.set(obj, bundle.getParcelableArray(key));

        } else if (Serializable.class.isAssignableFrom(type)) {
            field.set(obj, bundle.getSerializable(key));

        } else {
            throw new RuntimeException("Unsupported field type: " + field.getName()
                    + ", " + type.getSimpleName());
        }

        bundle.remove(key);
    }
}