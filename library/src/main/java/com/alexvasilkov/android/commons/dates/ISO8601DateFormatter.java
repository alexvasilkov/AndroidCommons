package com.alexvasilkov.android.commons.dates;

import java.text.ParseException;
import java.util.Date;

@SuppressWarnings("unused") // Public API
public class ISO8601DateFormatter {

    private static final ThreadSafeDateFormatter ISO8601_PARSER =
            new ThreadSafeDateFormatter("yyyy-MM-dd'T'HH:mm:ssZ");

    private static final ThreadSafeDateFormatter ISO8601_FORMATTER =
            new ThreadSafeDateFormatter("yyyy-MM-dd'T'HH:mm:ss", ThreadSafeDateFormatter.GMT);


    /**
     * Produces string in format <code>yyyy-MM-dd'T'HH:mm:ss'Z'</code>.
     */
    public static String formatISO8601(Date date) {
        return ISO8601_FORMATTER.format(date) + "Z";
    }

    /**
     * Parses dates in following formats:
     * <pre>
     *   yyyy-MM-dd
     *   yyyy-MM-dd'T'HH:mm:ss[.S+]
     *   yyyy-MM-dd'T'HH:mm:ss[.S+]Z
     *   yyyy-MM-dd'T'HH:mm:ss[.S+]±hh[[:]mm]
     * </pre>
     *
     * Examples:
     * <pre>
     *   1988-03-06
     *   1988-03-06T12:33:14
     *   1988-03-06T12:33:14.12345
     *   1988-03-06T12:33:14Z
     *   1988-03-06T12:33:14,123Z
     *   1988-03-06T12:33:14+03
     *   1988-03-06T12:33:14.123+03
     *   1988-03-06T12:33:14-04:30
     *   1988-03-06T12:33:14-0430
     *   1988-03-06T12:33:14,1-04:30
     * </pre>
     */
    public static Date parseISO8601(String str) throws ParseException {
        if (str == null) {
            throw new NullPointerException("String to parse is null");
        }

        if (str.charAt(str.length() - 1) == 'Z') {
            str = str.substring(0, str.length() - 1) + "+0000";
        }

        // Stripping milliseconds
        int dotIndex = str.indexOf('.');
        if (dotIndex == -1) {
            dotIndex = str.indexOf(',');
        }

        if (dotIndex != -1) {
            int endIndex = str.indexOf('+', dotIndex);
            if (endIndex == -1) {
                endIndex = str.indexOf('-', dotIndex);
            }
            if (endIndex == -1) {
                endIndex = str.length();
            }
            str = str.substring(0, dotIndex) + str.substring(endIndex);
        }

        // Adding missing parts
        if (str.length() == "yyyy-MM-dd".length()) { // No time
            str += "T00:00:00+0000";
        } else if (str.length() == "yyyy-MM-ddTHH:mm:ss".length()) { // No timezone
            str += "+0000";
        } else if (str.length() == "yyyy-MM-ddTHH:mm:ss±hh".length()) { // No minutes in timezone
            str += "00";
        }

        if (str.charAt(str.length() - 3) == ':') {
            str = str.substring(0, str.length() - 3) + str.substring(str.length() - 2);
        }

        // At this point we should have string in format yyyy-MM-dd'T'HH:mm:ss±hhmm

        return ISO8601_PARSER.parse(str);
    }

    private ISO8601DateFormatter() {}

}
