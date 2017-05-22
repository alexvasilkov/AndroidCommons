package com.alexvasilkov.android.commons.nav.builders;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

public class PickContactIntentBuilder extends IntentBuilder {

    private static final String SONY_ACTION_PICK_EMAIL =
            "com.sonyericsson.android.socialphonebook.action.PICK";

    private final String mimeType;

    public PickContactIntentBuilder(@NonNull Navigate navigator, @NonNull String mimeType) {
        super(navigator);

        this.mimeType = mimeType;
    }

    @Override
    protected IntentHolder build(Navigate navigator) {
        // On some Sony devices there is no app that may handle Intent.ACTION_PICK for emails,
        // but it should work with special action for standalone Sony contacts app.

        Intent intent = buildIntent(SONY_ACTION_PICK_EMAIL);
        if (!isValidIntent(intent)) {
            intent = buildIntent(Intent.ACTION_PICK);
        }

        return new IntentHolder(navigator, intent);
    }

    private Intent buildIntent(String action) {
        final Intent intent = new Intent(action, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(mimeType);
        return intent;
    }

    private boolean isValidIntent(Intent intent) {
        return getContext().getPackageManager().resolveActivity(intent, 0) != null;
    }

}
