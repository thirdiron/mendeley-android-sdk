package com.mendeley.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// TODO: encapsulate the parsing/formatting of the dates in the classes that perform it
// https://jira.mendeley.com/browse/AND-345
public class DateUtils {
    // ISO 8601 format, used by the Mendeley web API for timestamps.
    public final static SimpleDateFormat mendeleyApiDateFormat;

    static {
        mendeleyApiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        mendeleyApiDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Returns a {@link java.util.Date} given one String with a timestamp in the format used by the web API.
     *
     * @param date in the format used by Mendeley web API
     * @return parsed date
     * @throws java.text.ParseException
     */
    public static Date parseMendeleyApiTimestamp(String date) throws ParseException {
        return mendeleyApiDateFormat.parse(date);
    }

    public static String formatMendeleyApiTimestamp(Date date) {
        return mendeleyApiDateFormat.format(date);
    }

    /**
     * Returns a {@link java.util.Date} given one String with a timestamp in the format used by headers
     * of the responses of the Mendeley web API.
     *
     * @param dateHeader in the format used by Mendeley web API
     * @return parsed date
     * @throws java.text.ParseException
     */
    public static Date parseDateInHeader(String dateHeader) throws ParseException {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss 'GMT'");
        simpledateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpledateformat.parse(dateHeader);
    }
}
