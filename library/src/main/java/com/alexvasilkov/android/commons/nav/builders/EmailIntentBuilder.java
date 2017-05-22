package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class EmailIntentBuilder extends IntentBuilder {

    private static final String MIME_TYPE_HTML = "text/html";
    private static final String MIME_TYPE_TEXT = "text/plain";
    private static final String MIME_TYPE_EMAIL = "message/rfc822";

    private String[] toEmails;
    private String[] ccEmails;
    private String[] bccEmails;
    private String subject;
    private Spanned body;
    private String mimeType;
    private Uri attachedFileUri;

    public EmailIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }

    public EmailIntentBuilder toEmails(String... toEmails) {
        this.toEmails = toEmails;
        return this;
    }

    public EmailIntentBuilder ccEmails(String... ccEmails) {
        this.ccEmails = ccEmails;
        return this;
    }

    public EmailIntentBuilder bccEmails(String... bccEmails) {
        this.bccEmails = bccEmails;
        return this;
    }

    public EmailIntentBuilder subject(@Nullable String subject) {
        this.subject = subject;
        return this;
    }

    public EmailIntentBuilder body(@Nullable Spanned body) {
        this.body = body;
        return this;
    }

    public EmailIntentBuilder body(@Nullable String body) {
        return body(body == null ? null : new SpannableString(body));
    }

    public EmailIntentBuilder attachment(@Nullable Uri attachedFileUri) {
        this.attachedFileUri = attachedFileUri;
        return this;
    }

    public EmailIntentBuilder mimeType(@Nullable String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public EmailIntentBuilder asHtml() {
        return mimeType(MIME_TYPE_HTML);
    }

    public EmailIntentBuilder asPlainText() {
        return mimeType(MIME_TYPE_TEXT);
    }


    @Override
    protected IntentHolder build(Navigate navigator) {
        final Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType(mimeType == null ? MIME_TYPE_EMAIL : mimeType);

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
        if (attachedFileUri != null) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, attachedFileUri);
        }

        return null;
    }

}
