package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class PlayIntentBuilder extends IntentBuilder {

    private String appPackage;

    public PlayIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }

    public PlayIntentBuilder app(@NonNull String appPackage) {
        this.appPackage = appPackage;
        return this;
    }

    @Override
    protected IntentHolder build(Navigate navigator) {
        if (appPackage == null) {
            throw new NullPointerException("Missing app package name when launching Google Play");
        }

        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appPackage));

        final Intent fallback = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + appPackage));

        return new IntentHolder(navigator, intent, fallback);
    }

}
