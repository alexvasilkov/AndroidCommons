package com.alexvasilkov.fluffycommons.converters;

import android.util.Log;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ConvertUtils {

    /**
     * Converting array of convertable items into ArrayList of target items
     *
     * @param array Array to convert
     */
    public static <T, J extends Convertable<T>> ArrayList<T> convertToList(J[] array) {
        if (array == null) return null;
        ArrayList<T> list = new ArrayList<T>(array.length);
        for (J json : array) {
            try {
                T item = json.convert();
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
     * Shourtcat for {@link #listToArray(java.util.List) listToArray}({@link #convertToList(Convertable[]) convertToList(array)})
     */
    public static <T, J extends Convertable<T>> T[] convertToArray(J[] array) {
        return listToArray(convertToList(array));
    }

    /**
     * Converting array of convertable items into array of target items<br/>
     * Shourtcat for {@link #listToArray(java.util.List, Class) listToArray}({@link #convertToList(Convertable[]) convertToList(array)}, <code>clazz</code>)
     */
    public static <T, J extends Convertable<T>> T[] convertToArray(J[] array, Class<T> clazz) {
        return listToArray(convertToList(array), clazz);
    }

    /**
     * Searches for enum of given class with given name (ingnoring case)
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
     * Converting {@link java.util.List List} into array<br/>
     * Note: array type is determined by class of the first non-null element in the list.
     * If there is no elements or all elements are null, null will be returned.<br/>
     * Elements in the list should be exactly of requested type, not it's descendants.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] listToArray(List<T> list) {
        if (list == null) return null;

        T item = null;
        for (T i : list) {
            if (i != null) {
                item = i;
                break;
            }
        }

        return item == null ? null : listToArray(list, (Class<T>) item.getClass());
    }

    /**
     * Converting {@link java.util.List List} into array of given type
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] listToArray(List<T> list, Class<T> clazz) {
        if (list == null) return null;
        return list.toArray((T[]) Array.newInstance(clazz, list.size()));
    }

}
