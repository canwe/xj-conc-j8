package xj_conc.ch15_optimizations.exercise_15_3_1;

import xj_conc.ch15_optimizations.exercise_15_1_1.*;

/**
 * TODO: Make 6x faster for all data sets
 */
public class FastValidatorImpl implements Validator {
    public boolean checkInteger(String testInteger) {
        /*
        * The function must check that a given string is
        *
        * an integer
        * not the empty string or null
        * an integer greater than 10
        * an integer between 2 and 100000 inclusive
        * That the first digit is 3.
         */
        boolean s_check = null != testInteger; // not empty
        int length = s_check ? testInteger.length() : 0;
        s_check = s_check && length > 1 && length < 6; // greater than ten less than 100000
        s_check = s_check && testInteger.charAt(0) == '3'; // first digit is 3
        if (s_check)
            for (int i = 1; i < length; i++) {
                char c = testInteger.charAt(i);
                s_check = s_check && c >= '0' && c <= '9';
                if (!s_check) break;
            }
        return s_check;
    }
}
