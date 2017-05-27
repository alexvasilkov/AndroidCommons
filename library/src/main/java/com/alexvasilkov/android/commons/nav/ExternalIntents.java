package com.alexvasilkov.android.commons.nav;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;

import com.alexvasilkov.android.commons.nav.builders.AddToCalendarIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.BrowserIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.CustomTabsIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.DialIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.EmailIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.PickContactIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.PickImageIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.PlayIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.ShareIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.SmsIntentBuilder;
import com.alexvasilkov.android.commons.nav.builders.YouTubeIntentBuilder;
import com.alexvasilkov.android.commons.nav.handlers.PickImageHandler;
import com.alexvasilkov.android.commons.nav.handlers.PickPhoneHandler;

@SuppressWarnings("unused") // Public API
public class ExternalIntents {


    private final Navigate navigator;

    ExternalIntents(Navigate navigator) {
        this.navigator = navigator;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ex) {
            return false;
        }
    }

    /**
     * Returns builder for "add calendar event" request.
     */
    public AddToCalendarIntentBuilder addEvent() {
        return new AddToCalendarIntentBuilder(navigator);
    }

    public IntentHolder app(String packageName) {
        final Intent launcher = navigator.getContext().getPackageManager()
                .getLaunchIntentForPackage(packageName);
        return new IntentHolder(navigator, launcher);
    }

    /**
     * Returns builder to open url in a browser.
     */
    public BrowserIntentBuilder browser() {
        return new BrowserIntentBuilder(navigator);
    }

    /**
     * Returns builder to open url using Chrome custom tabs.<br/>
     * Note, that corresponding library should be added as dependency to your project.
     */
    public CustomTabsIntentBuilder customTabs() {
        return new CustomTabsIntentBuilder(navigator);
    }

    /**
     * Returns builder to open dialer with pre-filled phone number.
     */
    public DialIntentBuilder dial() {
        return new DialIntentBuilder(navigator);
    }

    /**
     * Returns builder for "send email" request.
     */
    public EmailIntentBuilder email() {
        return new EmailIntentBuilder(navigator);
    }

    /**
     * Returns builder to open Google Play details screen for specified app.<br/>
     * Falls back to browser if Google Play app is not found.
     */
    public PlayIntentBuilder googlePlay() {
        return new PlayIntentBuilder(navigator);
    }

    /**
     * Opens phone picker activity.<br/>
     * Use {@link PickPhoneHandler#onResult(Context, int, Intent)} to handle results.
     */
    public PickContactIntentBuilder pickEmail() {
        return new PickContactIntentBuilder(navigator,
                ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
    }

    /**
     * Opens phone picker activity.<br/>
     * Use {@link PickPhoneHandler#onResult(Context, int, Intent)} to handle results.
     */
    public PickContactIntentBuilder pickPhone() {
        return new PickContactIntentBuilder(navigator,
                ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
    }

    /**
     * Opens image picker activity.<br/>
     * Use {@link PickImageHandler#onResult(Context, int, Intent)} to handle results.
     */
    public PickImageIntentBuilder pickPhoto() {
        return new PickImageIntentBuilder(navigator);
    }

    /**
     * Returns builder for sharing request.<br/>
     * Uses Intent.ACTION_SEND action and "plain/text" mime type.
     */
    public ShareIntentBuilder share() {
        return new ShareIntentBuilder(navigator);
    }

    /**
     * Returns builder for sms sending request.
     */
    public SmsIntentBuilder sms() {
        return new SmsIntentBuilder(navigator);
    }

    /**
     * Returns builder to open YouTube app with given video id.<br/>
     * Falls back to browser if YouTube app is not found.
     */
    public YouTubeIntentBuilder youTube() {
        return new YouTubeIntentBuilder(navigator);
    }

    public void start(Intent intent) {
        new IntentHolder(navigator, intent).start();
    }

}
