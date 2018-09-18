package xj_conc.ch02_thread_safety.solution_2_2;

import net.jcip.annotations.*;

import java.text.*;
import java.util.*;

/**
 * Solve thread safety by using stack confinement.
 */
@ThreadSafe
public class DateFormatter {
    public String format(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public Date parse(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }
}