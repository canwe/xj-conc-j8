package xj_conc.ch02_thread_safety.solution_2_4;

import net.jcip.annotations.*;

import java.text.*;
import java.time.*;
import java.time.format.*;

/**
 * Solve thread safety by using object confinement.
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