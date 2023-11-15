package com.caipiao.modules.common.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MatchStopTimeUtil {

    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static String getStopPrintTime(String matchDateTime){

        LocalDateTime parse = LocalDateTime.parse(matchDateTime, dtf);

        DayOfWeek dayOfWeek = parse.getDayOfWeek();
        int hour = parse.getHour();

        if(dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY)){
            if(hour>11 && hour < 23){
                return parse.minusMinutes(5).format(dtf1);
            }else{
                //昨天晚上22点或者23点
                parse = parse.minusDays(1);
                if(dayOfWeek.equals(DayOfWeek.SUNDAY)){
                    //昨晚23点
                    parse = parse.withHour(22);
                }else{
                    parse = parse.withHour(21);
                }
                parse = parse.withMinute(55).withSecond(0);
                return parse.format(dtf1);
            }
        }else{

            if(hour>11 && hour < 22){
                return parse.minusMinutes(5).format(dtf1);
            }else{
                //昨天晚上22点或者23点
                parse = parse.minusDays(1);
                if(dayOfWeek == DayOfWeek.MONDAY){
                    //昨晚23点
                    parse = parse.withHour(22);
                }else{
                    parse = parse.withHour(21);
                }
                parse = parse.withMinute(55).withSecond(0);
                return parse.format(dtf1);
            }
        }
    }


    /**
     * 计算打印截止时间
     * @param stopPrintTime
     * @return
     */
    public static Date getStopPrintTime(Date stopPrintTime){
        //星期几
        int i = DateUtil.dayOfWeek(stopPrintTime);

        int hour = DateUtil.hour(stopPrintTime, true);

        /**
         * 周六和周日
         */
        if(i == 1 || i == 7){
            if(hour>11 && hour < 23){
                return new Date(stopPrintTime.getTime()-(long) 5*60*1000);
            }else{
                //昨天晚上22点或者23点
                DateTime dateTime = DateUtil.offsetDay(stopPrintTime, -1);
                if(i == 1){
                    //昨晚23点
                    dateTime.setField(DateField.HOUR_OF_DAY, 22);
                }else{
                    dateTime.setField(DateField.HOUR_OF_DAY, 21);
                }
                dateTime.setField(DateField.MINUTE, 55);
                dateTime.setField(DateField.SECOND, 0);
                dateTime.setField(DateField.MILLISECOND, 0);
                return dateTime;
            }
        }else{

            if(hour>11 && hour < 22){
                return new Date(stopPrintTime.getTime()-(long) 5*60*1000);
            }else{
                //昨天晚上22点或者23点
                DateTime dateTime = DateUtil.offsetDay(stopPrintTime, -1);
                if(i == 2){
                    //昨晚23点
                    dateTime.setField(DateField.HOUR_OF_DAY, 22);
                }else{
                    dateTime.setField(DateField.HOUR_OF_DAY, 21);
                }
                dateTime.setField(DateField.MINUTE, 55);
                dateTime.setField(DateField.SECOND, 0);
                dateTime.setField(DateField.MILLISECOND, 0);
                return dateTime;
            }
        }
    }

    public static void main(String[] args) {

        LocalDateTime parse = LocalDateTime.parse("2022-06-04 13:13:00", dtf);

        DayOfWeek dayOfWeek = parse.getDayOfWeek();


        System.out.println(dayOfWeek.equals(DayOfWeek.SATURDAY));


        System.out.println(parse.format(dtf));

        System.out.println(parse.getHour());


        System.out.println("-----------------");

        parse = parse.minusDays(1).withHour(22).withMinute(55);

        System.out.println(parse.format(dtf));

        String stopPrintTime = getStopPrintTime("2022-06-04 03:13:00");

        System.out.println(stopPrintTime);


    }

}
