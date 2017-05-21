package com.alexvasilkov.android.commons.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class ContextHelper {

    private ContextHelper() {}

    public static Activity asActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return asActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

    public static Application asApplication(Context context) {
        return asApplicationInternal(context.getApplicationContext());
    }

    private static Application asApplicationInternal(Context context) {
        if (context instanceof Application) {
            return (Application) context;
        } else if (context instanceof ContextWrapper) {
            return asApplicationInternal(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

}
