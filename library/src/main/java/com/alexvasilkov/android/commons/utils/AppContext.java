package com.alexvasilkov.android.commons.utils;

import android.content.Context;

public class AppContext {

    private static Context sAppContext;

    public static void init(Context context) {
        sAppContext = context.getApplicationContext();
    }

    public static Context get() {
        if (sAppContext == null) throw new RuntimeException("AppContext was not initialized with init(context) method");
        return sAppContext;
    }

}
