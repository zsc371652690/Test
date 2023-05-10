package com.wellness.qa.util.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import lombok.SneakyThrows;
import org.junit.platform.commons.util.StringUtils;

public class DateUtil {
    public static final String PATTERN = "yyyy-MM-dd";

    public DateUtil() {
    }

    public static String format(Date date) {
        return format((Date)date, (String)null);
    }

    public static String format(Date date, String pattern) {
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String format(LocalDateTime localDateTime) {
        return format((LocalDateTime)localDateTime, (String)null);
    }

    public static String format(LocalDateTime localDateTime, String pattern) {
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dtf);
    }

    public static Date parseDate(String dateString) {
        return parseDate(dateString, (String)null);
    }

    public static Date parseDate(String dateString, String pattern) {
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd";
        }

        DateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;

        try {
            date = sdf.parse(dateString);
            return date;
        } catch (ParseException var8) {
            var8.printStackTrace();
            return date;
        } finally {
            ;
        }
    }

    public static String strToDateFormat(String date, String dateFormat, String tarFormat) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(StringUtils.isBlank(dateFormat) ? "yyyyMMdd" : dateFormat);
        formatter.setLenient(false);
        Date newDate = formatter.parse(date);
        formatter = new SimpleDateFormat(StringUtils.isBlank(tarFormat) ? "yyyy-MM-dd" : tarFormat);
        return formatter.format(newDate);
    }

    public static String dateFormat(String ofPattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(ofPattern));
    }

    public static LocalDateTime parseLocalDateTime(String localDateTimeString) {
        return LocalDateTime.parse(localDateTimeString, (DateTimeFormatter)null);
    }

    public static LocalDateTime parseLocalDateTime(String localDateTimeString, String pattern) {
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(localDateTimeString, dtf);
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date addTime(Date originDate, int amount, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originDate);
        calendar.add(field, amount);
        return calendar.getTime();
    }


    public  static String dateAddDays(String dateStr,int amount) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateStart = dateFormat.parse(dateStr);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dateStart);
        rightNow.add(Calendar.DAY_OF_YEAR,amount);//日期+amount
        Date endTime = rightNow.getTime();
        return dateFormat.format(endTime);
    }

    public  static String dateAddMonths(String dateStr,int amount) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateStart = dateFormat.parse(dateStr);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dateStart);
        rightNow.add(Calendar.MONTH,amount);//日期+amount
        Date endTime = rightNow.getTime();
        return dateFormat.format(endTime);
    }

    public  static String dateAddYears(String dateStr,int amount) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateStart = dateFormat.parse(dateStr);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dateStart);
        rightNow.add(Calendar.YEAR,amount);//日期+amount
        Date endTime = rightNow.getTime();
        return dateFormat.format(endTime);
    }

    public static int getAge(Date birthDay, Date dateToCompare) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToCompare);
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("Birthday is after date " + dateToCompare + " !");
        } else {
            int year = cal.get(1);
            int month = cal.get(2);
            int dayOfMonth = cal.get(5);
            boolean isLastDayOfMonth = dayOfMonth == cal.getActualMaximum(5);
            cal.setTime(birthDay);
            int age = year - cal.get(1);
            int monthBirth = cal.get(2);
            if (month == monthBirth) {
                int dayOfMonthBirth = cal.get(5);
                boolean isLastDayOfMonthBirth = dayOfMonthBirth == cal.getActualMaximum(5);
                if ((!isLastDayOfMonth || !isLastDayOfMonthBirth) && dayOfMonth < dayOfMonthBirth) {
                    --age;
                }
            } else if (month < monthBirth) {
                --age;
            }

            return age;
        }
    }

    public static boolean treatedAsEqualWithDelta(Date date1, Date date2, long deltaMilliSecond) {
        return date1 == date2 || date1 != null && date2 != null && Math.abs(date1.getTime() - date2.getTime()) < deltaMilliSecond;
    }
}

