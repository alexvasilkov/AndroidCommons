package com.alexvasilkov.android.commons.nav.handlers;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // Public API
public class PickImageHandler {

    /**
     * Call this method from {@link Activity#onActivityResult(int, int, Intent)} method to
     * get picked contact info.
     */
    @Nullable
    public static List<Uri> onResult(@NonNull Context context, int resultCode,
            @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return null;
        }

        ClipData clip = null;

        if (Build.VERSION.SDK_INT >= 18) {
            clip = data.getClipData();
        }

        final List<Uri> uris;

        if (clip != null) {
            uris = new ArrayList<>();
            for (int i = 0, size = clip.getItemCount(); i < size; i++) {
                uris.add(clip.getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            uris = Collections.singletonList(data.getData());
        } else {
            uris = Collections.emptyList();
        }

        // Trying to get persistable read permissions
        if (Build.VERSION.SDK_INT >= 19) {
            final int readFlag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            if ((data.getFlags() & readFlag) == readFlag) {
                for (Uri uri : uris) {
                    try {
                        context.getContentResolver().takePersistableUriPermission(uri, readFlag);
                    } catch (Exception ignored) {
                        // Persistable permission was not granted, ignoring
                    }
                }
            }
        }

        return uris;
    }

}
