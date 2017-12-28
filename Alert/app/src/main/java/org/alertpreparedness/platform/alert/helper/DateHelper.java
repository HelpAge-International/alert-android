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

}
