package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.customtabs.CustomTabsIntent;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;
import com.alexvasilkov.android.commons.ui.ResourcesHelper;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class CustomTabsIntentBuilder extends IntentBuilder {

    private final CustomTabsIntent.Builder builder;
    private Uri uri;

    public CustomTabsIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);

        builder = new CustomTabsIntent.Builder();

        try {
            final int primaryColorId = getContext().getResources()
                    .getIdentifier("colorPrimary", "attr", getContext().getPackageName());
            builder.setToolbarColor(ResourcesHelper.getAttrColor(getContext(), primaryColorId));
        } catch (Exception ignored) {
        }
    }


    public CustomTabsIntentBuilder color(@ColorInt int color) {
        builder.setToolbarColor(color);
        return this;
    }

    public CustomTabsIntentBuilder colorRes(@ColorRes int colorResId) {
        builder.setToolbarColor(ResourcesHelper.getColor(getContext(), colorResId));
        return this;
    }

    public CustomTabsIntentBuilder enableUrlBarHiding() {
        builder.enableUrlBarHiding();
        return this;
    }


    public CustomTabsIntentBuilder url(@NonNull String url) {
        this.uri = Uri.parse(url);
        return this;
    }

    public CustomTabsIntentBuilder url(@StringRes int urlResId) {
        return url(getContext().getString(urlResId));
    }

    public CustomTabsIntentBuilder uri(@NonNull Uri uri) {
        this.uri = uri;
        return this;
    }


    @Override
    protected IntentHolder build(Navigate navigator) {
        if (uri == null) {
            throw new NullPointerException("Missing uri when launching custom tabs");
        }

        final Intent intent = builder.build().intent;
        intent.setData(uri);
        return new IntentHolder(navigator, intent);
    }

}
