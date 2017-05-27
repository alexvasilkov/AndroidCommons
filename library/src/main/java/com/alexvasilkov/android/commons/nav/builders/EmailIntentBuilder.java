package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Note, that there is no option to properly send HTML body, so support for this was dropped.
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class EmailIntentBuilder extends IntentBuilder {

    private static final String MIME_TYPE_EMAIL = "message/rfc822";

    private String[] toEmails;
    private String[] ccEmails;
    private String[] bccEmails;
    private String subject;
    private String body;
    private Uri[] attachedFileUris;

    public EmailIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }

    public EmailIntentBuilder toEmails(String... toEmails) {
        this.toEmails = toEmails;
        return this;
    }

    /**
     * Note: may not work in some clients (e.g. does not work in Outlook).
     */
    public EmailIntentBuilder ccEmails(String... ccEmails) {
        this.ccEmails = ccEmails;
        return this;
    }

    /**
     * Note: may not work in some clients (e.g. does not work in Outlook).
     */
    public EmailIntentBuilder bccEmails(String... bccEmails) {
        this.bccEmails = bccEmails;
        return this;
    }

    public EmailIntentBuilder subject(@Nullable String subject) {
        this.subject = subject;
        return this;
    }

    public EmailIntentBuilder body(@Nullable String body) {
        this.body = body;
        return this;
    }

    public EmailIntentBuilder attachment(Uri... attachedFileUris) {
        this.attachedFileUris = attachedFileUris;
        return this;
    }


    @Override
    protected IntentHolder build(Navigate navigator) {
        final Intent intent;

        if (attachedFileUris == null || attachedFileUris.length == 0) {
            intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));

        } else if (attachedFileUris.length == 1) {
            intent = new Intent(Intent.ACTION_SEND);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, attachedFileUris[0]);

            intent.setType(MIME_TYPE_EMAIL);
        } else {
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                    new ArrayList<>(Arrays.asList(attachedFileUris)));

            intent.setType(MIME_TYPE_EMAIL);
        }

        if (toEmails != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, toEmails);
        }
        if (ccEmails != null) {
            intent.putExtra(Intent.EXTRA_CC, ccEmails);
        }
        if (bccEmails != null) {
            intent.putExtra(Intent.EXTRA_BCC, bccEmails);
        }
        if (subject != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (body != null) {
            intent.putExtra(Intent.EXTRA_TEXT, body);
        }

        return new IntentHolder(navigator, intent);
    }

}
