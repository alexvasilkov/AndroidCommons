package com.alexvasilkov.android.commons.state;

import com.google.gson.Gson;

class GsonHelper {

    private static Boolean hasGson;
    private static Object gson;

    static boolean hasGson() {
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

    static Gson get() {
        if (gson == null) gson = new Gson();
        return (Gson) gson;
    }

}
