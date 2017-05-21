package com.alexvasilkov.android.commons.dates;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Thread safe date formatter. Each thread will have it's own instance of {@link SimpleDateFormat}
 * formatter.<br/>
 * This class provides basic methods to parse / format dates: {@link #parse(String)},
 * {@link #format(java.util.Date)}, {@link #format(long)}.
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class ThreadSafeDateFormatter {

    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private final String pattern;
    private final Locale locale;
    private final TimeZone tz;
    private final DateFormatSymbols symbols;

    private ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {

        protected SimpleDateFormat initialValue() {
            final Locale locale = ThreadSafeDateFormatter.this.locale == null
                    ? Locale.getDefault() : ThreadSafeDateFormatter.this.locale;
            final SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);

            if (tz != null) {
                formatter.setTimeZone(tz);
            }

            if (symbols != null) {
                formatter.setDateFormatSymbols(symbols);
            }
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

    public ThreadSafeDateFormatter(String pattern, Locale locale, TimeZone tz,
            DateFormatSymbols symbols) {
        this.pattern = pattern;
        this.locale = locale;
        this.tz = tz;
        this.symbols = symbols;
    }

    public Date parse(String str) throws ParseException {
        return formatter.get().parse(str);
    }

    public String format(Date date) {
        return formatter.get().format(date);
    }

    public String format(long date) {
        return format(new Date(date));
    }

}
