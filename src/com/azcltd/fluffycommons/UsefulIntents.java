package com.azcltd.fluffycommons;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.text.SpannableString;
import android.text.Spanned;

import java.util.TimeZone;

public class UsefulIntents {

    public static final String MIME_TYPE_HTML = "text/html";
    public static final String MIME_TYPE_TEXT = "text/plain";
    public static final String MIME_TYPE_EMAIL = "message/rfc822";

    /**
     * Starts email activity. See also
     * {@link #sendEmail(android.content.Context, String[], String, android.text.Spanned, String, String) sendEmail}.
     */
    public static void sendEmail(Context context, String email, String subject, String content) {
        sendEmail(context, email == null ? null : new String[]{email}, subject,
                content == null ? null : new SpannableString(content), null, null);
    }

    /**
     * Starts email activity, email content is considered as Html (i.e. Html.fromHtml(...)).<br/>
     * See also {@link #sendEmail(android.content.Context, String[], String, android.text.Spanned, String, String) sendEmail}.
     */
    public static void sendEmailAsHtml(Context context, String email, String subject, Spanned content) {
        sendEmail(context, email == null ? null : new String[]{email}, subject, content, MIME_TYPE_HTML, null);
    }

    /**
     * Starts email activity
     *
     * @param context         Context
     * @param toEmails        Destination emails
     * @param subject         Email subject
     * @param content         Email content
     * @param mimeType        Mime type of email content
     * @param attachedFileUri Attached file URI
     */
    public static void sendEmail(Context context, String[] toEmails, String subject, Spanned content, String mimeType,
                                 String attachedFileUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType == null ? MIME_TYPE_EMAIL : mimeType);
        if (toEmails != null) intent.putExtra(Intent.EXTRA_EMAIL, toEmails);
        if (subject != null) intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        if (content != null) intent.putExtra(Intent.EXTRA_TEXT, content);
        if (attachedFileUri != null) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(attachedFileUri));
        }
        startActivity(context, intent, false);
    }

    /**
     * Starts SMS activity.
     *
     * @param context Context
     * @param content SMS content
     */
    public static void sendSms(Context context, String content) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        if (content != null) intent.putExtra("sms_body", content);
        startActivity(context, intent, false);
    }

    /**
     * Starts "add calendar event" activity.
     *
     * @param context     Context
     * @param beginTime   Use 0 for no begin time
     * @param endTime     Use 0 for no end time
     * @param tz          Use null for no time zone
     * @param isAllDay    Marks event as all day long
     * @param title       Use null for no title
     * @param description Use null for no description
     * @param location    Use null for no location
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void addCalendarEvent(Context context, long beginTime, long endTime, TimeZone tz, boolean isAllDay,
                                        String title, String description, String location) {
        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT >= 14) {
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

        startActivity(context, intent, false);
    }

    /**
     * Opens given url in the default browser.
     *
     * @param context Context
     * @param url     Web-page url
     */
    public static void openWebBrowser(Context context, String url) {
        startActivity(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)), false);
    }

    /**
     * @param context Context
     * @param title   Use null for no title
     * @param text    Text to share
     */
    public static void share(Context context, String title, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(MIME_TYPE_TEXT);

        if (title != null) intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(context, intent, true);
    }

    public static void dial(Context context, String phone) {
        startActivity(context, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)), false);
    }

    public static void openGooglePlay(Context context, String appPackage) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
        boolean started = startActivity(context, intent, false);
        if (!started) openWebBrowser(context, "http://play.google.com/store/apps/details?id=" + appPackage);
    }

    private static boolean startActivity(Context context, Intent intent, boolean useChooser) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        try {
            context.startActivity(useChooser ? Intent.createChooser(intent, null) : intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private UsefulIntents() {
    }

}
