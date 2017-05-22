package com.alexvasilkov.android.commons.nav;

import android.content.Context;
import android.support.annotation.NonNull;

@SuppressWarnings("unused") // Public API
public abstract class IntentBuilder {

    private final Navigate navigator;

    public IntentBuilder(@NonNull Navigate navigator) {
        this.navigator = navigator;
    }

    protected abstract IntentHolder build(Navigate navigator);

    protected Context getContext() {
        return navigator.getContext();
    }

    public IntentHolder build() {
        return build(navigator);
    }

    public void start() {
        build().start();
    }

}
