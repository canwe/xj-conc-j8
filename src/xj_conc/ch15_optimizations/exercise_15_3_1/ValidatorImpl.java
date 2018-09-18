package xj_conc.ch15_optimizations.exercise_15_3_1;

import xj_conc.ch15_optimizations.exercise_15_1_1.*;

/**
 * DO NOT CHANGE
 */
public class ValidatorImpl implements Validator {
    public boolean checkInteger(String testInteger) {
        try {
            // fails if not a number
            Integer theInteger = new Integer(testInteger);
            return (!theInteger.toString().equals("")) && // not empty
                (theInteger > 10) && // greater than ten
                ((theInteger >= 2) && (theInteger <= 100000)) && // 2>=X<=100000
                // first digit is 3
                (theInteger.toString().charAt(0) == '3');
        } catch (NumberFormatException err) {
            return false;
        }
    }
}
