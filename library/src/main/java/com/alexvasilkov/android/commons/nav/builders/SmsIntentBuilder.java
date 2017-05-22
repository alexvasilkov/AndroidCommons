package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class SmsIntentBuilder extends IntentBuilder {

    private static final String MIME_TYPE_TEXT = "text/plain";

    private String phone;
    private String text;

    public SmsIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }


    public SmsIntentBuilder phone(@Nullable String phone) {
        this.phone = phone;
        return this;
    }

    public SmsIntentBuilder phone(@StringRes int phoneResId) {
        return phone(getContext().getString(phoneResId));
    }

    public SmsIntentBuilder text(@Nullable String text) {
        this.text = text;
        return this;
    }

    public SmsIntentBuilder text(@StringRes int textResId) {
        return text(getContext().getString(textResId));
    }


    @Override
    protected IntentHolder build(Navigate navigator) {
        final Intent intent;

        if (Build.VERSION.SDK_INT < 19) {

            intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
            if (phone != null && phone.length() > 0) {
                intent.putExtra("address", phone);
            }
            if (text != null) {
                intent.putExtra("sms_body", text);
            }

        } else {

            if (phone == null || phone.length() == 0) {
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType(MIME_TYPE_TEXT);
                if (text != null) {
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                }
            } else {
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(phone)));
                if (text != null) {
                    intent.putExtra("sms_body", text);
                }
            }

            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getContext());
            if (defaultSmsPackageName != null) {
                intent.setPackage(defaultSmsPackageName);
            }

        }

        return new IntentHolder(navigator, intent);
    }

}
