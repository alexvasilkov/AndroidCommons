package com.azcltd.fluffycommons.utils;

import android.app.Activity;
import android.view.View;

public final class Views {

    @SuppressWarnings("unchecked")
    public static <T extends View> T find(View parent, int viewId) {
        return (T) parent.findViewById(viewId);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T find(Activity activity, int viewId) {
        return (T) activity.findViewById(viewId);
    }

    private Views() {
    }

}
