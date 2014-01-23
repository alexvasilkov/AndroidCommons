package com.azcltd.fluffycommons.utils;

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
    private final TimeZone mTz;
    private final DateFormatSymbols mSymbols;

    private ThreadLocal<SimpleDateFormat> mFormatter = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat formatter = new SimpleDateFormat(mPattern, Locale.getDefault());
            if (mTz != null) formatter.setTimeZone(mTz);
            if (mSymbols != null) formatter.setDateFormatSymbols(mSymbols);
            return formatter;
        }
    };

    public ThreadSafeDateFormatter(String pattern) {
        this(pattern, null, null);
    }

    public ThreadSafeDateFormatter(String pattern, TimeZone tz) {
        this(pattern, tz, null);
    }

    public ThreadSafeDateFormatter(String pattern, DateFormatSymbols symbols) {
        this(pattern, null, symbols);
    }

    public ThreadSafeDateFormatter(String pattern, TimeZone tz, DateFormatSymbols symbols) {
        mPattern = pattern;
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
