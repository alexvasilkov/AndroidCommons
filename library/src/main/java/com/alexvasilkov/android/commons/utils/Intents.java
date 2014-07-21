package com.alexvasilkov.android.commons.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.text.SpannableString;
import android.text.Spanned;

import java.util.TimeZone;

public class Intents {

    public static final String MIME_TYPE_HTML = "text/html";
    public static final String MIME_TYPE_TEXT = "text/plain";
    public static final String MIME_TYPE_EMAIL = "message/rfc822";

    public static Builder get(Context context) {
        return new Builder(context);
    }

    private Intents() {
    }

    public static class Builder {

        private final Context context;
        private boolean useChooser;
        private CharSequence chooserTitle;
        private Integer requestCode;

        private Builder(Context context) {
            this.context = context;
        }

        public Builder useChooser(boolean useChooser) {
            this.useChooser = useChooser;
            return this;
        }

        public Builder useChooser() {
            return useChooser(true);
        }

        public Builder chooserTitle(CharSequence chooserTitle) {
            this.chooserTitle = chooserTitle;
            return this;
        }

        public Builder requestCode(Integer requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        /**
         * Return builder for sharing request.<br/>
         * Opens chooser dialog (if useChooser() method is called before) with all apps that can share provided text.<br/>
         * Uses Intent.ACTION_SEND action and "plain/text" mime type.
         */
        public ShareBuilder share() {
            return new ShareBuilder(this);
        }

        /**
         * Opens given url in the default browser.
         */
        public boolean openWebBrowser(String url) {
            return open(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }

        /**
         * Opens standard dialer app with prefilled phone number.
         */
        public boolean dial(String phone) {
            return open(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
        }

        /**
         * Opens Google Play app details screen for specified package. Opens browser if Google Play app is not found.
         */
        public boolean openGooglePlay(String appPackage) {
            boolean started = open(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage)));
            if (!started) started = openWebBrowser("http://play.google.com/store/apps/details?id=" + appPackage);
            return started;
        }

        /**
         * Opens YouTube app with given video id.
         */
        public boolean openYouTube(String videoId) {
            boolean started = open(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vnd.youtube:%s", videoId))));
            if (!started) started = openWebBrowser(String.format("http://m.youtube.com/watch?v=%s", videoId));
            return started;
        }

        /**
         * Opens phone picker activity. See also {@link IntentsHandler#onPickPhoneResult(android.content.Context, android.content.Intent)}
         */
        public boolean pickPhoneNumber() {
            if (requestCode == null)
                throw new NullPointerException("Activity request code is required when picking phone number");

            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            return open(intent);
        }

        /**
         * Opens gallery image picker activity. You can get picked image's Uri using {@link android.content.Intent#getData()}
         * inside {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)} method.
         */
        public boolean pickPhotoFromGallery() {
            if (requestCode == null)
                throw new NullPointerException("Activity request code is required when picking image");

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            return open(intent);
        }

        /**
         * Returns builder for sms sending request.
         */
        public SmsBuilder sendSms() {
            return new SmsBuilder(this);
        }

        /**
         * Returns builder for "add calendar event" request.
         */
        public EmailBuilder sendEmail() {
            return new EmailBuilder(this);
        }

        /**
         * Returns builder for "add calendar event" request.
         */
        public AddToCalendarBuilder addToCalendar() {
            return new AddToCalendarBuilder(this);
        }

        public boolean open(Intent intent) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            try {
                startActivity(useChooser ? Intent.createChooser(intent, chooserTitle) : intent);
                return true;
            } catch (ActivityNotFoundException e) {
                try {
                    startActivity(Intent.createChooser(intent, chooserTitle));
                    return true;
                } catch (ActivityNotFoundException e2) {
                    e2.printStackTrace();
                    return false;
                }
            }
        }

        private void startActivity(Intent intent) {
            if (requestCode == null) {
                context.startActivity(intent);
            } else {
                ((Activity) context).startActivityForResult(intent, requestCode);
            }
        }

    }


    public static class ShareBuilder {
        private final Builder builder;

        private String title, text;

        private ShareBuilder(Builder builder) {
            this.builder = builder;
        }

        public ShareBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ShareBuilder text(String text) {
            this.text = text;
            return this;
        }

        public boolean open() {
            if (text == null) throw new NullPointerException("Sharing text cannot be null");

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(MIME_TYPE_TEXT);

            if (title != null) intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TEXT, text);

            return builder.open(intent);
        }
    }

    public static class SmsBuilder {
        private final Builder builder;

        private String phone, text;

        private SmsBuilder(Builder builder) {
            this.builder = builder;
        }

        public SmsBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public SmsBuilder text(String text) {
            this.text = text;
            return this;
        }

        public boolean open() {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (phone == null || phone.length() == 0) {
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType(MIME_TYPE_TEXT);
                    if (text != null) intent.putExtra(Intent.EXTRA_TEXT, text);
                } else {
                    intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(phone)));
                    if (text != null) intent.putExtra("sms_body", text);
                }

                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(builder.context);
                if (defaultSmsPackageName != null) intent.setPackage(defaultSmsPackageName);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("vnd.android-dir/mms-sms");
                if (phone != null && phone.length() > 0) intent.putExtra("address", phone);
                if (text != null) intent.putExtra("sms_body", text);
            }

            return builder.open(intent);
        }
    }

    public static class EmailBuilder {
        private final Builder builder;

        private String[] toEmails, ccEmails, bccEmails;
        private String subject;
        private Spanned body;
        private String mimeType;
        private String attachedFileUri;

        private EmailBuilder(Builder builder) {
            this.builder = builder;
        }

        public EmailBuilder toEmails(String... toEmails) {
            this.toEmails = toEmails;
            return this;
        }

        public EmailBuilder ccEmails(String... ccEmails) {
            this.ccEmails = ccEmails;
            return this;
        }

        public EmailBuilder bccEmails(String... bccEmails) {
            this.bccEmails = bccEmails;
            return this;
        }

        public EmailBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public EmailBuilder body(Spanned body) {
            this.body = body;
            return this;
        }

        public EmailBuilder body(String body) {
            this.body = new SpannableString(body);
            return this;
        }

        public EmailBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public EmailBuilder asHtml() {
            return mimeType(MIME_TYPE_HTML);
        }

        public EmailBuilder asPlainText() {
            return mimeType(MIME_TYPE_TEXT);
        }

        public EmailBuilder attachment(String attachedFileUri) {
            this.attachedFileUri = attachedFileUri;
            return this;
        }

        public boolean open() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(mimeType == null ? MIME_TYPE_EMAIL : mimeType);
            if (toEmails != null) intent.putExtra(Intent.EXTRA_EMAIL, toEmails);
            if (ccEmails != null) intent.putExtra(Intent.EXTRA_CC, ccEmails);
            if (bccEmails != null) intent.putExtra(Intent.EXTRA_BCC, bccEmails);
            if (subject != null) intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            if (body != null) intent.putExtra(Intent.EXTRA_TEXT, body);
            if (attachedFileUri != null) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(attachedFileUri));
            }

            return builder.open(intent);
        }
    }

    public static class AddToCalendarBuilder {
        private final Builder builder;

        private long beginTime, endTime;
        private TimeZone tz;
        private boolean isAllDay;
        private String title, description, location;

        private AddToCalendarBuilder(Builder builder) {
            this.builder = builder;
        }

        public AddToCalendarBuilder begin(long beginTime) {
            this.beginTime = beginTime;
            return this;
        }

        public AddToCalendarBuilder end(long endTime) {
            this.endTime = endTime;
            return this;
        }

        public AddToCalendarBuilder timezone(TimeZone tz) {
            this.tz = tz;
            return this;
        }

        public AddToCalendarBuilder allDay(boolean isAllDay) {
            this.isAllDay = isAllDay;
            return this;
        }

        public AddToCalendarBuilder allDay() {
            return allDay(true);
        }

        public AddToCalendarBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AddToCalendarBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AddToCalendarBuilder location(String location) {
            this.location = location;
            return this;
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public boolean open() {
            Intent intent = new Intent();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                intent.setAction(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
            } else {
                intent.setAction(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
            }

            if (tz != null) {
                // sending timezone is now working, so we will add timezone offset manually
                if (beginTime != 0) {
                    long offsetDiff = tz.getOffset(beginTime) - TimeZone.getDefault().getOffset(beginTime);
                    beginTime += offsetDiff;
                }
                if (endTime != 0) {
                    long offsetDiff = tz.getOffset(endTime) - TimeZone.getDefault().getOffset(endTime);
                    endTime += offsetDiff;
                }
            }

            if (beginTime == 0) {
                // begin time seems to be mandatory for some apps
                beginTime = System.currentTimeMillis();
            }

            if (endTime == 0) {
                // begin time seems to be mandatory for some apps
                endTime = beginTime + 60 * 60 * 1000;
            }

            if (beginTime != 0) intent.putExtra("beginTime", beginTime);
            if (endTime != 0) intent.putExtra("endTime", endTime);
            if (isAllDay) intent.putExtra("allDay", true);
            if (title != null) intent.putExtra("title", title);
            if (description != null) intent.putExtra("description", description);
            if (location != null) intent.putExtra("eventLocation", location);

            return builder.open(intent);
        }
    }

}
