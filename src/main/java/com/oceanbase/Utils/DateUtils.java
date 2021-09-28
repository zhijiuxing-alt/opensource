package com.oceanbase.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateUtils {

    public final static String FORMAT_STRING2 = "EEE MMM dd HH:mm:ss yyyy z";

    public static Date parseTimeZone(String dateString) {
        try {
            //转换为date
            SimpleDateFormat sf1 = new SimpleDateFormat(FORMAT_STRING2, Locale.ENGLISH);
            Date date = sf1.parse(dateString);
            return date;
        } catch (Exception e) {
            throw new RuntimeException("时间转化格式错误" + "[dateString=" + dateString + "]");
        }
    }

}

