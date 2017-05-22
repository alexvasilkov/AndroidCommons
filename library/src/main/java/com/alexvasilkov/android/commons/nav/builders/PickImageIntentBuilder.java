package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.alexvasilkov.android.commons.nav.ExternalIntents;
import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class PickImageIntentBuilder extends IntentBuilder {

    private static final String PHOTOS_PACKAGE = "com.google.android.apps.photos";
    private static final String MIME_TYPE_IMAGES = "image/*";

    private boolean multiple;

    public PickImageIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }

    public PickImageIntentBuilder multiple(boolean multiple) {
        this.multiple = multiple;
        return this;
    }

    public PickImageIntentBuilder multiple() {
        return multiple(true);
    }

    @Override
    protected IntentHolder build(Navigate navigator) {
        if (navigator == null) {
            throw new NullPointerException(
                    "Activity request code is required when picking image");
        }

        final Intent intent;

        boolean photosInstalled = ExternalIntents.isAppInstalled(getContext(), PHOTOS_PACKAGE);

        if (photosInstalled) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setPackage(PHOTOS_PACKAGE);
        } else if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= 19) {
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }

        intent.setType(MIME_TYPE_IMAGES);

        if (Build.VERSION.SDK_INT >= 18) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple);
        }

        return new IntentHolder(navigator, intent);
    }

}
