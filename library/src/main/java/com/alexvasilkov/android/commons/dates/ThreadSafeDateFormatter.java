package com.alexvasilkov.android.commons.dates;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Thread safe date formatter. Each thread will have it's own instance of {@link SimpleDateFormat} formatter.<br/>
 * This class provides basic methods to parse / format dates: {@link #parse(String)}, {@link #format(java.util.Date)},
 * {@link #format(long)}.
 */
public class ThreadSafeDateFormatter {

    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private final String mPattern;
    private final Locale mLocale;
    private final TimeZone mTz;
    private final DateFormatSymbols mSymbols;

    private ThreadLocal<SimpleDateFormat> mFormatter = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            Locale locale = mLocale == null ? Locale.getDefault() : mLocale;
            SimpleDateFormat formatter = new SimpleDateFormat(mPattern, locale);
            if (mTz != null) formatter.setTimeZone(mTz);
            if (mSymbols != null) formatter.setDateFormatSymbols(mSymbols);
            return formatter;
        }
    };

    public ThreadSafeDateFormatter(String pattern) {
        this(pattern, null, null, null);
    }

    public ThreadSafeDateFormatter(String pattern, Locale locale) {
        this(pattern, locale, null, null);
    }

    public ThreadSafeDateFormatter(String pattern, TimeZone tz) {
        this(pattern, null, tz, null);
    }

    public ThreadSafeDateFormatter(String pattern, DateFormatSymbols symbols) {
        this(pattern, null, null, symbols);
    }

    public ThreadSafeDateFormatter(String pattern, Locale locale, TimeZone tz) {
        this(pattern, locale, tz, null);
    }

    public ThreadSafeDateFormatter(String pattern, Locale locale, TimeZone tz, DateFormatSymbols symbols) {
        mPattern = pattern;
        mLocale = locale;
        mTz = tz;
        mSymbols = symbols;
    }

    public Date parse(String str) throws ParseException {
        return mFormatter.get().parse(str);
    }

    public String format(Date date) {
        return mFormatter.get().format(date);
    }

    public String format(long date) {
        return format(new Date(date));
    }

}
