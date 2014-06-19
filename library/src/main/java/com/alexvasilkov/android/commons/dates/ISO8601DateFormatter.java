package com.alexvasilkov.android.commons.dates;

import java.text.ParseException;
import java.util.Date;

public class ISO8601DateFormatter {

    private static final ThreadSafeDateFormatter ISO8601_PARSER =
            new ThreadSafeDateFormatter("yyyy-MM-dd'T'HH:mm:ssZ");

    private static final ThreadSafeDateFormatter ISO8601_FORMATTER =
            new ThreadSafeDateFormatter("yyyy-MM-dd'T'HH:mm:ss", ThreadSafeDateFormatter.GMT);


    /**
     * Produces string in format <code>yyyy-MM-ddTHH:mm:ss+00:00</code>
     */
    public static String formatISO8601(Date date) {
        return ISO8601_FORMATTER.format(date) + "+00:00";
    }

    /**
     * Parses dates in following formats:
     * <pre>
     *   yyyy-MM-dd
     *   yyyy-MM-ddTHH:mm:ss[.S+]
     *   yyyy-MM-ddTHH:mm:ss[.S+]Z
     *   yyyy-MM-ddTHH:mm:ss[.S+]±hh[[:]mm]
     * </pre>
     */
    public static Date parseISO8601(String str) throws ParseException {
        if (str == null) throw new NullPointerException("String to parse is null");

        if (str.charAt(str.length() - 1) == 'Z') str = str.substring(0, str.length() - 1) + "+0000";

        // Stripping milliseconds
        int dotIndex = str.indexOf('.');
        if (dotIndex == -1) dotIndex = str.indexOf(',');

        if (dotIndex != -1) {
            int endIndex = str.indexOf('+', dotIndex);
            if (endIndex == -1) endIndex = str.indexOf('-', dotIndex);
            if (endIndex == -1) endIndex = str.length();
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

        if (str.charAt(str.length() - 3) == ':')
            str = str.substring(0, str.length() - 3) + str.substring(str.length() - 2);

        // At this point we should have string in format yyyy-MM-ddTHH:mm:ss±hhmm

        return ISO8601_PARSER.parse(str);
    }

//    public static void testParser() {
//        String[] tests = {
//                "1988-03-06",
//                "1988-03-06T12:33:14",
//                "1988-03-06T12:33:14.12345",
//                "1988-03-06T12:33:14Z",
//                "1988-03-06T12:33:14,123Z",
//                "1988-03-06T12:33:14+03",
//                "1988-03-06T12:33:14.123+03",
//                "1988-03-06T12:33:14-04:30",
//                "1988-03-06T12:33:14-0430",
//                "1988-03-06T12:33:14,1-04:30"
//        };
//
//        for (String str : tests) {
//            test(str);
//        }
//    }
//
//    private static void test(String str) {
//        try {
//            Date parsed = parseISO8601(str);
//            String back = formatISO8601(parsed);
//            Log.d("DATETEST", str + " -> " + back + " // " + parsed.toString());
//        } catch (Exception e) {
//            Log.e("DATETEST", "Can't parse date: " + str + " // " + e.getMessage());
//        }
//    }

}
