package xj_conc.ch02_thread_safety.exercise_2_4;

import net.jcip.annotations.*;

import java.text.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Solve thread safety by using the immutable parser object
 * DateTimeFormatter.ISO_LOCAL_DATE.  Your methods would now use LocalDate.
 */
@ThreadSafe
public class DateFormatter {
    private static final DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;

    public String format(LocalDate date) {
        return df.format(date);
    }

    public LocalDate parse(String date) throws ParseException {
        return LocalDate.parse(date, df);
    }
}
