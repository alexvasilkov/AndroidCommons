package com.alexvasilkov.android.commons.converters;

import android.util.Log;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ConvertUtils {

    /**
     * Converting value if it is not null
     */
    public static <T, P extends Convertable<T>> T convert(P value) throws ParseException {
        return value == null ? null : value.convert();
    }

    /**
     * Converting array of convertable items into ArrayList of target items.
     * Note: per item ParseException are ignored.
     *
     * @param array Array to convert
     */
    public static <T, J extends Convertable<T>> ArrayList<T> convert(J[] array) {
        return convert(array == null ? null : Arrays.asList(array));
    }

    /**
     * Converting collection of convertable items into ArrayList of target items.
     * Note: per item ParseException are ignored.
     *
     * @param collection Collection to convert
     */
    public static <T, J extends Convertable<T>> ArrayList<T> convert(Collection<J> collection) {
        if (collection == null) return null;

        ArrayList<T> list = new ArrayList<T>(collection.size());

        for (J json : collection) {
            try {
                T item = json == null ? null : json.convert();
                if (item != null) list.add(item);
            } catch (ParseException e) {
                Log.e("ConvertUtils", "Error converting item (" + json.getClass().getSimpleName() + ") : "
                        + e.getMessage());
            }
        }

        return list;
    }

    /**
     * Converting array of convertable items into array of target items<br/>
     * Shortcut for {@link #toArray(java.util.Collection) toArray}({@link #convert(Convertable[]) convert(array)})
     */
    public static <T, J extends Convertable<T>> T[] convertToArray(J[] array) {
        return toArray(convert(array));
    }

    /**
     * Converting array of convertable items into array of target items<br/>
     * Shortcut for {@link #toArray(java.util.Collection, Class) toArray}({@link #convert(Convertable[]) convert(array)}, <code>clazz</code>)
     */
    public static <T, J extends Convertable<T>> T[] convertToArray(J[] array, Class<T> clazz) {
        return toArray(convert(array), clazz);
    }

    /**
     * Converting collection of convertable items into array of target items<br/>
     * Shortcut for {@link #toArray(java.util.Collection) toArray}({@link #convert(java.util.Collection) convert(collection)})
     */
    public static <T, J extends Convertable<T>> T[] convertToArray(Collection<J> collection) {
        return toArray(convert(collection));
    }

    /**
     * Converting Collection of convertable items into array of target items<br/>
     * Shortcut for {@link #toArray(java.util.Collection, Class) toArray}({@link #convert(java.util.Collection) convert(array)}, <code>clazz</code>)
     */
    public static <T, J extends Convertable<T>> T[] convertToArray(Collection<J> collection, Class<T> clazz) {
        return toArray(convert(collection), clazz);
    }

    /**
     * Searches for enum of given class with given name (case insensitive)
     *
     * @param type         Enum class
     * @param name         Enum constant name
     * @param defaultValue Default value if no enum constant with given name is found
     */
    public static <T extends Enum<T>> T convert(Class<T> type, String name, T defaultValue) {
        if (name == null) return defaultValue;

        for (T e : type.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(name)) return e;
        }

        return defaultValue;
    }

    /**
     * Creates output object using provided creator. Returns null if obj null.
     */
    public static <IN, OUT> OUT create(IN obj, Creator<IN, OUT> creator) {
        return obj == null ? null : creator.create(obj);
    }

    /**
     * Creates list of output objects using provided creator
     */
    public static <IN, OUT> List<OUT> create(IN[] arr, Creator<IN, OUT> creator) {
        if (arr == null) return null;

        ArrayList<OUT> list = new ArrayList<OUT>(arr.length);

        for (IN value : arr) {
            OUT item = value == null ? null : creator.create(value);
            if (item != null) list.add(item);
        }

        return list;
    }

    /**
     * Creates list of output objects using provided creator
     */
    public static <IN, OUT> List<OUT> create(Collection<IN> collection, Creator<IN, OUT> creator) {
        if (collection == null) return null;

        ArrayList<OUT> list = new ArrayList<OUT>(collection.size());

        for (IN value : collection) {
            OUT item = value == null ? null : creator.create(value);
            if (item != null) list.add(item);
        }

        return list;
    }

    /**
     * Converting {@link java.util.List List} into array<br/>
     * Note: array type is determined by class of the first non-null element in the list.
     * If there is no elements or all elements are null, null will be returned.<br/>
     * Elements in the list should be exactly of requested type, not it's descendants.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> collection) {
        if (collection == null) return null;

        T item = null;
        for (T i : collection) {
            if (i != null) {
                item = i;
                break;
            }
        }

        return item == null ? null : toArray(collection, (Class<T>) item.getClass());
    }

    /**
     * Converting {@link java.util.List List} into array of given type
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> collection, Class<T> clazz) {
        if (collection == null) return null;
        return collection.toArray((T[]) Array.newInstance(clazz, collection.size()));
    }

}
