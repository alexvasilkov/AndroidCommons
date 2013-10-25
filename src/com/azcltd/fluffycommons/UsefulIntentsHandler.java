package com.azcltd.fluffycommons;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

public class UsefulIntentsHandler {

    public static ContactInfo onPickPhoneResult(Context context, Intent data) {
        if (data == null || data.getData() == null) return null;

        Cursor c = null;
        try {
            c = context.getContentResolver().query(data.getData(), new String[]{
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER},
                    null, null, null);

            if (c != null && c.moveToFirst()) {
                return new ContactInfo(c.getInt(0), c.getString(1), c.getString(2));
            }
        } finally {
            if (c != null) c.close();
        }

        return null;
    }

    public static class ContactInfo {
        public final int id;
        public final String name;
        public final String phone;

        public ContactInfo(int id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }
    }

}
