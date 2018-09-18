package xj_conc.ch02_thread_safety.solution_2_3;

import net.jcip.annotations.*;

import java.text.*;
import java.util.*;

/**
 * Solve thread safety by using object confinement.
 */
@ThreadSafe
public class DateFormatter {
    @GuardedBy("this")
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public synchronized String format(Date date) {
        return df.format(date);
    }

    public synchronized Date parse(String date) throws ParseException {
        return df.parse(date);
    }
}