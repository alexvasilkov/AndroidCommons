package com.alexvasilkov.android.commons.nav.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class PickEmailHandler {

    /**
     * Call this method from {@link Activity#onActivityResult(int, int, Intent)} method to
     * get picked contact info.
     */
    @Nullable
    public static Data onResult(@NonNull Context context, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(data.getData(),
                    new String[] {
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Email.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Email.ADDRESS
                    },
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return new Data(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public static class Data {
        public final int id;
        public final String name;
        public final String email;

        Data(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }

}
