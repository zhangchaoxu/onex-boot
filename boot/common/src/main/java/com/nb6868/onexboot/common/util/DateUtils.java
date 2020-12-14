package com.nb6868.onexboot.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DateUtils {

    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date 日期
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转Date
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * LocalDate转Date
     * 因为LocalDate不包含时间，所以转Date时，会默认转为当天的起始时间，00:00:00
     */
    public static Date localDate2Date(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * 日期解析
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回Date
     */
    public static Date parse(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转换成日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期的格式，如：DateUtils.DATE_TIME_PATTERN
     */
    public static Date stringToDate(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDate = LocalDateTime.parse(strDate, fmt);
        return Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 根据周数，获取开始日期、结束日期
     *
     * @param week 周期  0本周，-1上周，-2上上周，1下周，2下下周
     * @return 返回date[0]开始日期、date[1]结束日期
     */
    public static Date[] getWeekStartAndEnd(int week) {
        // 该周第一天
        LocalDateTime localDateTime = LocalDateTime.now().plusWeeks(week).with(DayOfWeek.MONDAY);
        Date beginDate = localDateTime2Date(localDateTime);
        Date endDate = localDateTime2Date(localDateTime.plusDays(6));
        return new Date[]{beginDate, endDate};
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date    日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, long seconds) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime2Date(localDateTime.plusSeconds(seconds));
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date    日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime2Date(localDateTime.plusMinutes(minutes));
    }

    /**
     * 对日期的【小时】进行加/减
     *
     * @param date  日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime2Date(localDateTime.plusHours(hours));
    }

    /**
     * 对日期的【天】进行加/减
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime2Date(localDateTime.plusDays(days));
    }

    /**
     * 对日期的【周】进行加/减
     *
     * @param date  日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime2Date(localDateTime.plusWeeks(weeks));
    }

    /**
     * 对日期的【月】进行加/减
     *
     * @param date   日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime2Date(localDateTime.plusMonths(months));
    }

    /**
     * 当前时间
     */
    public static String today() {
        LocalDate todayDate = LocalDate.now();
        return todayDate.toString();
    }

    /**
     * 当前时间
     */
    public static Date now() {
        return localDateTime2Date(LocalDateTime.now());
    }

    /**
     * 当前毫秒时间
     */
    public static long nowMillis() {
        return Instant.now().toEpochMilli();
    }

    /**
     * 获得时间差
     *
     * @param before
     * @param after
     * @return
     */
    public static long timeDiff(Date before, Date after) {
        return after.getTime() - before.getTime();
    }

    /**
     * 获得与当前时间的时间差
     *
     * @param before
     * @return
     */
    public static long timeDiff(Date before) {
        return timeDiff(before, now());
    }

}
