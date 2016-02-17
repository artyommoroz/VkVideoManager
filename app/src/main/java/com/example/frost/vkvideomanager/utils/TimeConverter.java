package com.example.frost.vkvideomanager.utils;

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
}
