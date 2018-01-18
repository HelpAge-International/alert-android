package org.alertpreparedness.platform.alert.helper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by faizmohideen on 22/12/2017.
 */

public class DateHelper {

    public static boolean isDueToday(Long milliseconds) {
        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(milliseconds);

        return today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isDueInWeek(Long milliseconds) {
        Calendar oneWeekAhead = Calendar.getInstance();
        oneWeekAhead.add(Calendar.DAY_OF_YEAR, 8); // 8 to make it at the end of the day
        oneWeekAhead.set(Calendar.HOUR, 0);
        oneWeekAhead.set(Calendar.MINUTE, 0);
        Calendar now = Calendar.getInstance();

        return oneWeekAhead.getTimeInMillis() >= milliseconds && milliseconds >= now.getTimeInMillis();
    }

    public static boolean itWasDue(Long milliseconds) {
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTimeInMillis(milliseconds);
        Calendar today = Calendar.getInstance();

        return dueDate.before(today);
    }

    public static boolean isInProgressWeek(Long milliseconds, int i) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(milliseconds);
        date.add(Calendar.DAY_OF_YEAR, i*7);
        date.set(Calendar.HOUR, 24);
        date.set(Calendar.MINUTE, 00);
        Calendar now = Calendar.getInstance();

        return date.after(now);
    }

    public static boolean isInProgressMonth(Long milliseconds, int i) {
        Calendar date = Calendar.getInstance();
        int month = 31;
        date.setTimeInMillis(milliseconds);
        date.add(Calendar.DAY_OF_YEAR, i*month);
        date.set(Calendar.HOUR, 0);
        date.set(Calendar.MINUTE, 0);
        Calendar now = Calendar.getInstance();

        return date.after(now);
    }

    public static Boolean isInProgressYear(Long milliseconds, int i) {
        Calendar date = Calendar.getInstance();
        int year = 365;
        date.setTimeInMillis(milliseconds);
        date.add(Calendar.DAY_OF_YEAR, i*year);
        date.set(Calendar.HOUR, 0);
        date.set(Calendar.MINUTE, 0);
        Calendar now = Calendar.getInstance();

        return date.after(now);
    }

    public static Boolean isInProgressDay(Long milliseconds, int i) {
        Calendar date = Calendar.getInstance();
        int day = 1;
        date.setTimeInMillis(milliseconds);
        date.add(Calendar.DAY_OF_YEAR, i*day);
        date.set(Calendar.HOUR, 0);
        date.set(Calendar.MINUTE, 0);
        Calendar now = Calendar.getInstance();

        return date.after(now);
    }

    public static Boolean isInProgressHour(Long milliseconds, int i) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(milliseconds);
        date.set(Calendar.HOUR, i);
        date.set(Calendar.MINUTE, 0);
        Calendar now = Calendar.getInstance();

        return date.after(now);
    }
}
