package com.alexvasilkov.android.commons.utils;

import android.os.Looper;

public final class Preconditions {

    public static void checkNotNull(Object object, String paramName) {
        if (object == null) {
            throw new NullPointerException(String.format("Parameter '%s' can not be null", paramName));
        }
    }

    public static void checkNotEmpty(CharSequence charSequence, String paramName) {
        if (charSequence == null || charSequence.length() == 0) {
            throw new RuntimeException(String.format("Parameter '%s' can not be null or empty", paramName));
        }
    }

    public static void checkInMainThread() {
        if (!isMainThread()) {
            throw new RuntimeException("You must execute this method in main thread");
        }
    }

    public static void checkInBackgroundThread() {
        if (isMainThread()) {
            throw new RuntimeException("You must execute this method in background thread");
        }
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private Preconditions() {
    }

}
