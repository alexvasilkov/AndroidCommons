package com.alexvasilkov.android.commons.utils;

import android.util.Log;
import com.alexvasilkov.android.commons.BuildConfig;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonHelper {

    private static Boolean hasGson;
    private static Object gson;

    public static boolean hasGson() {
        if (hasGson == null) {
            try {
                Class.forName("com.google.gson.Gson");
                hasGson = true;
            } catch (Exception e) {
                hasGson = false;
            }
        }
        return hasGson;
    }

    public static Gson get() {
        if (gson == null) gson = new Gson();
        return (Gson) gson;
    }

    public static String toJson(Object obj) {
        try {
            return obj == null ? null : get().toJson(obj);
        } catch (Exception e) {
            Log.e("GsonHelper", "Cannot convert object to JSON", e);
            return null;
        }
    }

    public static <T> T fromJson(String str, Class<T> clazz) {
        return fromJson(str, (Type) clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String str, Type type) {
        try {
            return str == null ? null : (T) get().fromJson(str, type);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("GsonHelper", "Cannot parse JSON to object", e);
            return null;
        }
    }

}
