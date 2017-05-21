package com.alexvasilkov.android.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Helper to set default locale to a specified value.<br/>
 * For example you may want to sync localization from strings.xml with system localization
 * (e.g. DateFormat, etc). This will help to avoid cases when part of the app is translated into
 * one language while some elements are translated into another (system one).<p/>
 * To use this helper you will need to set desired locale using {@link #setLocale(Locale)} or
 * {@link #setLanguage(String)} and then wrap context by overriding
 * {@link Activity#attachBaseContext} and wrapping context using {@link #wrap(Context)} method:
 * <pre>
 * {@code
 * @Override
 * protected void attachBaseContext(Context newBase) {
 *     super.attachBaseContext(LocalizationContextWrapper.wrap(newBase));
 * }
 * }
 * </pre>
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class LocalizationContext {

    private static Locale locale;

    private LocalizationContext() {}

    public static void setLanguage(@NonNull String lang) {
        setLocale(new Locale(lang));
    }

    /**
     * Sets default locale and locale for conext wrapping, see {@link #wrap(Context)}.
     */
    public static void setLocale(@NonNull Locale locale) {
        if (!locale.equals(Locale.getDefault())) {
            LocalizationContext.locale = locale;
            Locale.setDefault(locale);
        }
    }

    /**
     * Wrapping provided context with new localized context, using locale set in
     * {@link #setLocale(Locale)} or {@link #setLanguage(String)}.<br/>
     * New context should be provided in {@link Activity#attachBaseContext}.<p/>
     * See {@link LocalizationContext} for more details.
     */
    @NonNull
    public static Context wrap(@NonNull Context origContext) {
        return locale == null ? origContext : wrapWithLocale(origContext, locale);
    }

    @SuppressWarnings("deprecation")
    @NonNull
    private static Context wrapWithLocale(Context context, Locale locale) {
        if (Build.VERSION.SDK_INT < 25) {
            Resources res = context.getResources();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, res.getDisplayMetrics());
            return context;
        } else {
            Configuration conf = context.getResources().getConfiguration();
            conf.setLocale(locale);
            return context.createConfigurationContext(conf);
        }
    }

}
