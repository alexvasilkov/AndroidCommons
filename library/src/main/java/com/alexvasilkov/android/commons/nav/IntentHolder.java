package com.alexvasilkov.android.commons.nav;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class IntentHolder {

    private final Navigate navigator;
    private final Intent intent;
    private final Intent fallback;

    public IntentHolder(@NonNull Navigate navigator, @NonNull Intent intent, @Nullable Intent fallback) {
        this.navigator = navigator;
        this.intent = intent;
        this.fallback = fallback;
    }

    public IntentHolder(@NonNull Navigate navigator, @NonNull Intent intent) {
        this(navigator, intent, null);
    }

    @NonNull
    public Intent getIntent() {
        return intent;
    }

    @Nullable
    public Intent getFallback() {
        return fallback;
    }

    public void start() {
        navigator.startExternal(this);
    }

}
