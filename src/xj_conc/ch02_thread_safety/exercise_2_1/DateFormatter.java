package xj_conc.ch02_thread_safety.exercise_2_1;

import net.jcip.annotations.*;

import java.text.*;
import java.util.*;

/**
 * Solve thread safety by using thread confinement.
 */
@ThreadSafe
public class DateFormatter {
    private static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    public String format(Date date) {
        return df.get().format(date);
    }

    public Date parse(String date) throws ParseException {
        return df.get().parse(date);
    }
}
