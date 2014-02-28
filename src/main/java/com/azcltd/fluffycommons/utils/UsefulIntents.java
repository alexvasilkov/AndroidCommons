package com.azcltd.fluffycommons.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.Spanned;

import java.util.TimeZone;

public class UsefulIntents {

    public static final String MIME_TYPE_HTML = "text/html";
    public static final String MIME_TYPE_TEXT = "text/plain";
    public static final String MIME_TYPE_EMAIL = "message/rfc822";

    /**
     * Starts email activity. See also
     * {@link #sendEmail(android.content.Context, String[], String[], String[], String, android.text.Spanned, String, String) sendEmail}.
     */
    public static void sendEmail(Context context, String email, String subject, String content) {
        sendEmail(context, email == null ? null : new String[]{email}, null, null, subject, content);
    }

    /**
     * Starts email activity, email content is considered as Html (i.e. Html.fromHtml(...)).<br/>
     * See also {@link #sendEmail(android.content.Context, String[], String[], String[], String, android.text.Spanned, String, String) sendEmail}.
     */
    public static void sendEmailAsHtml(Context context, String email, String subject, Spanned content) {
        sendEmail(context, email == null ? null : new String[]{email}, null, null, subject, content, MIME_TYPE_HTML, null);
    }

    /**
     * Starts email activity. See also
     * {@link #sendEmail(android.content.Context, String[], String[], String[], String, android.text.Spanned, String, String) sendEmail}.
     */
    public static void sendEmail(Context context, String[] toEmails, String[] ccEmails, String[] bccEmails,
                                 String subject, String content) {
        sendEmail(context, toEmails, ccEmails, bccEmails, subject,
                content == null ? null : new SpannableString(content), null, null);
    }

    /**
     * Starts email activity
     *
     * @param context         Context
     * @param toEmails        Destination emails
     * @param ccEmails        Emails for CC field
     * @param bccEmails       Emails for BCC field
     * @param subject         Email subject
     * @param content         Email content
     * @param mimeType        Mime type of email content
     * @param attachedFileUri Attached file URI
     */
    public static void sendEmail(Context context, String[] toEmails, String[] ccEmails, String[] bccEmails,
                                 String subject, Spanned content, String mimeType, String attachedFileUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType == null ? MIME_TYPE_EMAIL : mimeType);
        if (toEmails != null) intent.putExtra(Intent.EXTRA_EMAIL, toEmails);
        if (ccEmails != null) intent.putExtra(Intent.EXTRA_CC, ccEmails);
        if (bccEmails != null) intent.putExtra(Intent.EXTRA_BCC, bccEmails);
        if (subject != null) intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        if (content != null) intent.putExtra(Intent.EXTRA_TEXT, content);
        if (attachedFileUri != null) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(attachedFileUri));
        }
        startExternalActivity(context, intent, false);
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
        startExternalActivity(context, intent, false);
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

        startExternalActivity(context, intent, false);
    }

    /**
     * Opens given url in the default browser.
     *
     * @param context Context
     * @param url     Web-page url
     */
    public static void openWebBrowser(Context context, String url) {
        startExternalActivity(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)), false);
    }

    /**
     * Opens chooser dialog with all apps that can share the text.
     * Uses Intent.ACTION_SEND action and "plain/text" mime type.
     *
     * @param context      Context
     * @param title        Use null for no title
     * @param text         Text to share
     * @param chooserTitle Title for chooser dialog
     */
    public static void share(Context context, String title, String text, CharSequence chooserTitle) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(MIME_TYPE_TEXT);

        if (title != null) intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startExternalActivity(context, intent, true, chooserTitle, null);
    }

    /**
     * Shortcut for {@link #share(android.content.Context, String, String, java.lang.CharSequence)
     * share(context, title, text, null)}
     */
    public static void share(Context context, String title, String text) {
        share(context, title, text, null);
    }

    /**
     * Opens standard dialer app with prefilled phone number.
     */
    public static void dial(Context context, String phone) {
        startExternalActivity(context, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)), false);
    }

    /**
     * Opens Google Play app details screen for specified package. Opens browser if Google Play app is not found.
     */
    public static void openGooglePlay(Context context, String appPackage) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
        boolean started = startExternalActivity(context, intent, false);
        if (!started) openWebBrowser(context, "http://play.google.com/store/apps/details?id=" + appPackage);
    }

    /**
     * Opens phone picker activity. See also {@link UsefulIntentsHandler#onPickPhoneResult(android.content.Context, android.content.Intent)}
     */
    public static void pickPhoneNumber(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startExternalActivity(activity, intent, false, null, requestCode);
    }

    /**
     * Shortcut to {@link #startExternalActivity(android.content.Context, android.content.Intent, boolean, java.lang.CharSequence, Integer)
     * startExternalActivity(context, intent, useChooser, null)}
     */
    public static boolean startExternalActivity(Context context, Intent intent, boolean useChooser) {
        return startExternalActivity(context, intent, useChooser, null, null);
    }

    /**
     * Starts external activity.<br/>
     * If useChooser == false but there were no activities to handle given intent chooser will be used to show empty dialog.<br/>
     * Flag FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET will be added to given intent to properly handle external activities.
     *
     * @param context      Context
     * @param intent       Intent to open
     * @param useChooser   Whether or not use default chooser dialog
     * @param chooserTitle Title for chooser dialog
     * @param requestCode  Request code if activity should be started with {@link Activity#startActivityForResult(android.content.Intent, int)}
     *                     or null for regular {@link Context#startActivity(android.content.Intent)} call.
     *                     Note that you should pass Activity as context param if requestCode is not null.
     * @return <code>true<code/> if intent was started
     */
    public static boolean startExternalActivity(Context context, Intent intent, boolean useChooser,
                                                CharSequence chooserTitle, Integer requestCode) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        try {
            startActivity(context, useChooser ? Intent.createChooser(intent, chooserTitle) : intent, requestCode);
            return true;
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(context, Intent.createChooser(intent, chooserTitle), requestCode);
                return true;
            } catch (ActivityNotFoundException e2) {
                e2.printStackTrace();
                return false;
            }
        }
    }

    private static void startActivity(Context context, Intent intent, Integer requestCode) {
        if (requestCode == null) {
            context.startActivity(intent);
        } else {
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
    }

    private UsefulIntents() {
    }

}
