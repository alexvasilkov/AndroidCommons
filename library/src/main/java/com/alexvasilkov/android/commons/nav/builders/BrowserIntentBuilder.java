package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class BrowserIntentBuilder extends IntentBuilder {

    private Uri uri;

    public BrowserIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }

    public BrowserIntentBuilder url(@NonNull String url) {
        this.uri = Uri.parse(url);
        return this;
    }

    public BrowserIntentBuilder url(@StringRes int urlResId) {
        return url(getContext().getString(urlResId));
    }

    public BrowserIntentBuilder uri(@NonNull Uri uri) {
        this.uri = uri;
        return this;
    }

    @Override
    protected IntentHolder build(Navigate navigator) {
        if (uri == null) {
            throw new NullPointerException("Missing uri when launching browser");
        }
        return new IntentHolder(navigator, new Intent(Intent.ACTION_VIEW, uri));
    }

}
