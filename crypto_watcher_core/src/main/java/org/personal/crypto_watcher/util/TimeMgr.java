package org.personal.crypto_watcher.util;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TimeMgr {

    private static DateTime time;
    private static final String TIME_FMT = "MM/dd/yyyy HH:mm";
    private static final String DATE_FMT = "MM/dd/yyyy";

    public static void setTime(){

        time = new DateTime().toDateTime(DateTimeZone.forID("America/New_York"));
    }

    public static void init(){

        if(time == null){
            setTime();
        }
    }


    public static DateTime getTime(){

        return time;
    }

    public static String getDateStr(){

        return time.toString(DATE_FMT);
    }

    public static DateTime getOneMinBeforeTime(){

        return time.minusMinutes(1);
    }

    public static DateTime getFiveMinBeforeTime(){

        return time.minusMinutes(5);
    }

    public static DateTime getTenMinBeforeTime(){

        return time.minusMinutes(10);
    }

    public static DateTime getThirtyMinBeforeTime(){

        return time.minusMinutes(30);
    }

    public static DateTime getOneHourBeforeTime(){

        return time.minusHours(1);
    }

    public static DateTime getOneDayBeforeTime(){

        return time.minusDays(1);
    }

    public static String getTimeStr(){

        return time.toString(TIME_FMT);
    }

    public static String fmtTime(DateTime userTime){

        return userTime.toString(TIME_FMT);
    }
}
