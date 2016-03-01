package com.example.frost.vkvideomanager.utils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeConverter {

    public static String secondsToHHmmss(int seconds) {
//        TimeZone tz = TimeZone.getTimeZone("UTC");
//        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
//        df.setTimeZone(tz);
//        String time = df.format(new Date(secondTime*1000L));
//        return time;
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
//        Locale locale = new Locale("ru", "RU");
        long timeStampMillis = timeStamp * 1000L;
        Date date = new Date(timeStampMillis);
        SimpleDateFormat simpleDateFormat;
        if (System.currentTimeMillis() - timeStampMillis < 86400000) {
            simpleDateFormat = new SimpleDateFormat(" 'сегодня в' H:mm", myDateFormatSymbols);
        } else if (System.currentTimeMillis() - timeStampMillis > 86400000
                && System.currentTimeMillis() - timeStampMillis < 86400000 * 2) {
            simpleDateFormat = new SimpleDateFormat(" 'вчера в' H:mm", myDateFormatSymbols);
        } else {
            simpleDateFormat = new SimpleDateFormat("dd MMM 'в' H:mm", myDateFormatSymbols);
        }
        return simpleDateFormat.format(date);
    }

    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"янв", "фев", "мар", "апр", "мая", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"};
        }
    };
}
