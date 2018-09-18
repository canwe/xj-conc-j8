package xj_conc.ch15_optimizations.solution_15_3_1;

import xj_conc.ch15_optimizations.exercise_15_1_1.*;

// Clever solution modeled on the regex "3[0-9]{1,4}"
public class JulienValidatorImpl implements Validator {
    public boolean checkInteger(String testInteger) {
        int length = testInteger == null ? 0 : testInteger.length();
        return length > 1
            && testInteger.charAt(0) == '3'
            && isDigit(testInteger.charAt(1))
            && (length <= 2 || isDigit(testInteger.charAt(2)))
            && (length <= 3 || isDigit(testInteger.charAt(3)))
            && (length <= 4 || isDigit(testInteger.charAt(4)))
            && (length <= 5);
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }
}
