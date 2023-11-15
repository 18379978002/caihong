package com.caipiao.common.utils;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理
 *
 */
public class DateUtils {
    /** 时间格式(yyyy-MM-dd) */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    public final static String DATE_PATTERN_YM = "yyyy-MM";
    public final static String DATE_PATTERN_YMCN = "yyyyMM";
    public final static String DATE_PATTERN_YCN = "yyyy";
    public final static String DATE_PATTERNCN = "yyyyMMdd";
    /** 时间格式(yyyy-MM-dd HH:mm:ss) */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_TIME_PATTERN_HM = "yyyy-MM-dd HH:mm";
    public final static String DATE_TIME_PATTERN_YMDHMS = "yyyyMMddHHmmss";
    //验证日期是yyyy-MM-dd支持闰年的正则表达式
    public static final String YMD_REXP = "((\\d{2}(([02468][048])|([13579][26]))[\\-]((((0?[13578])|(1[02]))[\\-]((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-]((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-]((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-]((((0?[13578])|(1[02]))[\\-]((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-]((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-]((0?[1-9])|(1[0-9])|(2[0-8]))))))";
    //验证日期是yyyy-MM-dd HH:mm:ss支持闰年的正则表达式
    public static final String YMDHMS_REXP = "((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))";
    public static final Calendar cal = Calendar.getInstance();
    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @param pattern  格式，如：DateUtils.DATE_TIME_PATTERN
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
     * 字串转化成日期.
     *
     * @param date date
     * @param pattern pattern
     * @return Date
     */
    public static Date str2Date(String date, String pattern) {
        if (date == null || date.trim() == "" || pattern == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 获取日期月份有多少天
     * @param date
     * @return
     */
    public static Integer getDaysOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     *
     * 功能描述: 根据日期获取星期几
     *
     * @auther: xiaoyinandan
     * @date: 2019/6/20 下午1:46
     * @param: [date]
     * @return: java.lang.Integer
     *
     */
    public static Integer getWeekOfDate(Date date) {

        Integer[] weekDays = {1, 2, 3, 4, 5, 6, 7};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 字符串转换成日期
     * @param strDate 日期字符串
     * @param pattern 日期的格式，如：DateUtils.DATE_TIME_PATTERN
     */
    public static Date stringToDate(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.parseLocalDateTime(strDate).toDate();
    }

    /**
     * 根据周数，获取开始日期、结束日期
     * @param week  周期  0本周，-1上周，-2上上周，1下周，2下下周
     * @return 返回date[0]开始日期、date[1]结束日期
     */
    public static Date[] getWeekStartAndEnd(int week) {
        DateTime dateTime = new DateTime();
        LocalDate date = new LocalDate(dateTime.plusWeeks(week));

        date = date.dayOfWeek().withMinimumValue();
        Date beginDate = date.toDate();
        Date endDate = date.plusDays(6).toDate();
        return new Date[]{beginDate, endDate};
    }

    /**
     * 一天的开始时间
     * @param date
     * @return
     */
    public static Date startTimeOfDay(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.withTime(0, 0, 0, 0).toDate();
    }

    /**
     * 一天的最后一秒
     * @param date
     * @return
     */
    public static Date endTimeOfDay(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.withTime(23, 59, 59, 0).toDate();
    }

    /**
     * 获取当月第一天
     * @param date
     * @return
     */
    public static Date firstDayOfMonth(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.dayOfMonth().withMinimumValue().withTime(0, 0, 0, 0).toDate();
    }

    /**
     * 获取上个月的第一天
     */
    public static Date firstDayOfLastMonth(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.minusMonths(1).dayOfMonth().withMinimumValue().withTime(0, 0, 0, 0).toDate();
    }

    public static Date lastDayOfMonth(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 0).toDate();
    }

    /**
     * 获取上个月的最后一天
     */
    public static Date lastDayOfLastMonth(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.minusMonths(1).dayOfMonth().withMaximumValue().withTime(23, 59, 59, 0).toDate();
    }

    /**
     * 获取传入日期所在周的最后一天
     * @param date
     * @return
     */
    public static Date lastDayWithWeek(Date date){
        cal.setTime(date);
        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        cal.add(Calendar.DAY_OF_WEEK, 6);
        return cal.getTime();
    }

    public static Date lastDayWithMonth(Date date){
        cal.setTime(date);
        final int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, last);
        return cal.getTime();
    }

    /**
     * 对日期的【秒】进行加/减
     * @param date 日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, int seconds) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusSeconds(seconds).toDate();
    }

    /**
     * 对日期的【分钟】进行加/减
     * @param date 日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minutes).toDate();
    }

    /**
     * 对日期的【小时】进行加/减
     * @param date 日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusHours(hours).toDate();
    }

    /**
     * 对日期的【天】进行加/减
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusDays(days).toDate();
    }

    /**
     * 对日期的【周】进行加/减
     * @param date 日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusWeeks(weeks).toDate();
    }

    /**
     * 对日期的【月】进行加/减
     * @param date 日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMonths(months).toDate();
    }

    /**
     * 对日期的【年】进行加/减
     * @param date 日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addDateYears(Date date, int years) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(years).toDate();
    }

    /**
     * 获取当前时间到指定时间相差多少秒
     * @param endTime
     * @return
     */
    public static int getSecondsToDate(Date endTime){
        DateTime nowDate = new DateTime();
        DateTime endDateTime = new DateTime(endTime);
        return Seconds.secondsBetween(nowDate, endDateTime).getSeconds();
    }

    public static Date getStartTime(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.withTime(0,0,0,0).toDate();
    }

    public static Date getEndTime(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.withTime(23,59,59,999).toDate();
    }

    public static int getMonthDiff(Date d1, Date d2) {
         String result = "";
         Calendar c1 = Calendar.getInstance();
         Calendar c2 = Calendar.getInstance();
         c1.setTime(d1);
         c2.setTime(d2);

         int year1 = c1.get(Calendar.YEAR);
         int year2 = c2.get(Calendar.YEAR);
         int month1 = c1.get(Calendar.MONTH);
         int month2 = c2.get(Calendar.MONTH);
         int day1 = c1.get(Calendar.DAY_OF_MONTH);
         int day2 = c2.get(Calendar.DAY_OF_MONTH);

         // 获取年的差值 
        int yearInterval = year1 - year2;

        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2){
            yearInterval--;
        }

        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2){
            monthInterval--;
        }

        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;

    }


    public static Integer getDaysBetweenDate(Date startDate, Date endDate){
        return Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays();
    }



    /** 计算两个日期相差的周数 **/
    public static Integer getBetweenWeeks(Date startDate,Date endDate){
        long between_days=(endDate.getTime()-startDate.getTime())/(1000*3600*24);
        Double days=Double.parseDouble(String.valueOf(between_days));
        if((days/7)>0 && (days/7)<=1){
            //不满一周的按一周算
            return 1;
        }else if(days/7>1){
            int day=days.intValue();
            if(day%7>0){
                return day/7+1;
            }else{
                return day/7;
            }
        }else if((days/7)==0){
            return 0;
        }else{
            //负数返还null
            return null;
        }
    }

    public static void main(String[] args) {
        int s = getMonthDiff(addDateMonths(new Date(), -14), new Date());
        String ss = "";
        if(s >= 12){
            int sss = s / 12;
            ss += sss + "年" + (s - sss * 12) + "个月";
        }else{
            ss = s + "个月";
        }
        System.out.println(ss);

        Date date = new Date();

        Date date1 = str2Date("2024-03-18 22:22:22", DATE_TIME_PATTERN);
        System.out.println(getBetweenWeeks(date,date1));
    }
}
