package com.alexvasilkov.android.commons.dates;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedDateFormatter extends SimpleDateFormat {

    private static final long serialVersionUID = 7394095110062211869L;

    private static final Pattern PATTERN = Pattern.compile("\\{(\\w+):(.+)\\}");

    public AdvancedDateFormatter(String pattern) {
        super(pattern);
    }

    public AdvancedDateFormatter(String template, Locale locale) {
        super(template, locale);
    }

    @Override
    public StringBuffer format(Date date, StringBuffer buffer, FieldPosition fieldPos) {
        StringBuffer formatted = super.format(date, buffer, fieldPos);
        StringBuffer result = new StringBuffer(formatted.length());

        Matcher m = PATTERN.matcher(formatted);
        String command, value;
        while (m.find()) {
            command = m.group(1);
            value = m.group(2);
            if ("lower".equals(command)) {
                m.appendReplacement(result, value.toLowerCase());
            } else if ("upper".equals(command)) {
                m.appendReplacement(result, value.toUpperCase());
            }
        }
        m.appendTail(result);

        return result;
    }

}