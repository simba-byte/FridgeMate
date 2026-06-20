package com.example.fridgemate.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class DateUtils {
    public static final String PATTERN = "yyyy-MM-dd";

    // SimpleDateFormat 不是线程安全的，使用 ThreadLocal 避免后台线程并发问题。
    private static final ThreadLocal<SimpleDateFormat> FORMAT = ThreadLocal.withInitial(
            () -> new SimpleDateFormat(PATTERN, Locale.CHINA));

    private DateUtils() {
    }

    public static String today() {
        return format(new Date());
    }

    public static String daysFromToday(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return format(calendar.getTime());
    }

    public static String format(Date date) {
        return FORMAT.get().format(date);
    }

    public static Date parse(String value) {
        try {
            return FORMAT.get().parse(value);
        } catch (ParseException | NullPointerException e) {
            return new Date();
        }
    }

    public static int daysUntil(String expireDate) {
        // 先把“今天”也格式化到日期粒度，避免时分秒影响天数计算。
        Date today = parse(today());
        Date expire = parse(expireDate);
        long diff = expire.getTime() - today.getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }

    public static boolean isExpired(String expireDate) {
        return daysUntil(expireDate) < 0;
    }

    public static boolean isExpiringSoon(String expireDate) {
        int days = daysUntil(expireDate);
        return days >= 0 && days <= 3;
    }
}
