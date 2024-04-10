package com.mocaphk.backend.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static String now() {
        return dateTimeFormatter.format(LocalDateTime.now());
    }

    public static String currentYear() {
        return Integer.toString(LocalDateTime.now().getYear());
    }

    public static LocalDateTime parse(String date) {
        return LocalDateTime.parse(date, dateTimeFormatter);
    }

    public static String toCron(LocalDateTime time) {
        return String.format("%d %d %d %d %d ?",
                time.getSecond(),
                time.getMinute(),
                time.getHour(),
                time.getDayOfMonth(),
                time.getMonthValue()
        );
    }
}
