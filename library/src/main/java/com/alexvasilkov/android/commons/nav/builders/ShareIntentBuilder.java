package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

@SuppressWarnings("unused") // Public API
public class ShareIntentBuilder extends IntentBuilder {

    private static final String MIME_TYPE_TEXT = "text/plain";

    private String text;
    private String title;

    public ShareIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }

    public ShareIntentBuilder text(@NonNull String text) {
        this.text = text;
        return this;
    }

    public ShareIntentBuilder text(@StringRes int textResId) {
        this.text = getContext().getString(textResId);
        return this;
    }

    public ShareIntentBuilder title(@Nullable String title) {
        this.title = title;
        return this;
    }

    public ShareIntentBuilder title(@StringRes int titleResId) {
        this.title = getContext().getString(titleResId);
        return this;
    }

    @Override
    protected IntentHolder build(Navigate navigator) {
        if (text == null) {
            throw new NullPointerException("Text to share cannot be null");
        }

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(MIME_TYPE_TEXT);

        if (title != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
        }
        intent.putExtra(Intent.EXTRA_TEXT, text);

        // Using chooser is essential when sharing, we don't want to fallback to any default app.
        return new IntentHolder(navigator, Intent.createChooser(intent, null));
    }

}
