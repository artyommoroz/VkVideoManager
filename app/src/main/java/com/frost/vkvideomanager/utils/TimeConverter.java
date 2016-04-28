package com.frost.vkvideomanager.utils;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TimeConverter {

    public static String secondsToHHmmss(int seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        String time;
        if (h < 1) {
            time = String.format("%2d:%02d", m,s);
        } else {
            time = String.format("%d:%02d:%02d", h,m,s);
        }
        return time;
    }

    public static String getFormattedDate(long timeStamp) {
        Locale locale = new Locale("ru", "RU");
        long timeStampMillis = timeStamp * 1000L;
        Date date = new Date(timeStampMillis);
        SimpleDateFormat simpleDateFormat;
        if (System.currentTimeMillis() - timeStampMillis < 86400000) {
            simpleDateFormat = new SimpleDateFormat("'сегодня в' H:mm");
        } else if (System.currentTimeMillis() - timeStampMillis > 86400000
                && System.currentTimeMillis() - timeStampMillis < 86400000 * 2) {
            simpleDateFormat = new SimpleDateFormat("'вчера в' H:mm");
        } else if (System.currentTimeMillis() - timeStampMillis > 864000004 * 2
                && System.currentTimeMillis() - timeStampMillis < 31 * 86400000) {
            simpleDateFormat = new SimpleDateFormat("dd MMM y", locale);
        } else {
            simpleDateFormat = new SimpleDateFormat("dd MMM 'в' H:mm", locale);
        }
        return simpleDateFormat.format(date);
    }

    public static String getViewsWithRightEnding(int viewsNumber) {
        String textNumber = String.valueOf(viewsNumber);
        String ending = "";
        if (textNumber.endsWith("2") || textNumber.endsWith("3") || textNumber.endsWith("4")) {
            ending = "a";
        } else if (textNumber.endsWith("5") || textNumber.endsWith("6") || textNumber.endsWith("7") ||
                textNumber.endsWith("8") || textNumber.endsWith("9") || textNumber.endsWith("0") ||
                textNumber.endsWith("11") || textNumber.endsWith("12") || textNumber.endsWith("13") ||
                textNumber.endsWith("14")) {
            ending = "ов";
        }
        return NumberFormat.getNumberInstance(Locale.FRENCH).format(viewsNumber) + " " + "просмотр" + ending;
    }

    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"янв", "фев", "мар", "апр", "мая", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"};
        }
    };
}
