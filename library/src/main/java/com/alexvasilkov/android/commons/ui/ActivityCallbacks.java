package com.alexvasilkov.android.commons.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

@SuppressWarnings("unused") // Public API
public class ActivityCallbacks {

    private ActivityCallbacks() {}

    public static void register(@NonNull Context context,
            @NonNull Application.ActivityLifecycleCallbacks listener) {
        ContextHelper.asApplication(context).registerActivityLifecycleCallbacks(listener);
    }

    public static void unregister(@NonNull Context context,
            @NonNull Application.ActivityLifecycleCallbacks listener) {
        ContextHelper.asApplication(context).unregisterActivityLifecycleCallbacks(listener);
    }


    public static class Callbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

        @Override
        public void onActivityStarted(Activity activity) {}

        @Override
        public void onActivityResumed(Activity activity) {}

        @Override
        public void onActivityPaused(Activity activity) {}

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}

    }

}
