package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class DialIntentBuilder extends IntentBuilder {

    private String phone;

    public DialIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }

    public DialIntentBuilder phone(@NonNull String phone) {
        this.phone = phone;
        return this;
    }

    public DialIntentBuilder phone(@StringRes int phoneResId) {
        return phone(getContext().getString(phoneResId));
    }

    @Override
    protected IntentHolder build(Navigate navigator) {
        if (phone == null) {
            throw new NullPointerException("Missing phone number when launching dialer");
        }

        final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        return new IntentHolder(navigator, intent);
    }

}
