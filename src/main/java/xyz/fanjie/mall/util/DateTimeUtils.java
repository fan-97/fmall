package xyz.fanjie.mall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 日期字符串互转工具类
 */
public class DateTimeUtils {
    private static final String STANDER_FORMAT = "yyyy-MM-dd hh:mm:ss";

    /**
     * 日期转字符串
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date,String formatStr){
        if(date==null){
            return StringUtils.EMPTY;
        }

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }
    public static String dateToStr(Date date){
        if(date==null){
            return StringUtils.EMPTY;
        }

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDER_FORMAT);
    }

    /**
     * 字符串转日期格式
     * @param dateStr
     * @param formatStr
     * @return
     */
    public static Date strToDate(String dateStr,String formatStr){
        DateTimeFormatter format = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = format.parseDateTime(formatStr);
        return dateTime.toDate();
    }

    public static Date strToDate(String dateStr){
        DateTimeFormatter format = DateTimeFormat.forPattern(STANDER_FORMAT);
        DateTime dateTime = format.parseDateTime(STANDER_FORMAT);
        return dateTime.toDate();
    }
}
