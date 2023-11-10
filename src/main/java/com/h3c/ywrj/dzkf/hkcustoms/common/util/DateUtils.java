package com.h3c.ywrj.dzkf.hkcustoms.common.util;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by @author Jeff on 2019-12-20 10:10
 */
public final class DateUtils {
    public static final String DATE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss:SSS";
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_3 = "yyyyMMdd";
    public static final String DATE_FORMAT_4 = "m/d/yyyy HH:mm";

    public static final String TIME_ZONE = "GMT+8";

    public static final SimpleDateFormat SDF1 = new SimpleDateFormat(DATE_FORMAT_1);
    public static final SimpleDateFormat SDF2 = new SimpleDateFormat(DATE_FORMAT_2);
    private static final SimpleDateFormat SDF3 = new SimpleDateFormat(DATE_FORMAT_3);

    public static final String convertDateToString(@NotNull Date date) {
        return SDF3.format(date);
    }

    public static final Date convertStringToDate(@NotNull String time) {
        try {
            return SDF2.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }
}
