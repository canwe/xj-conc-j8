package xj_conc.ch15_optimizations.solution_15_3_1;

import xj_conc.ch15_optimizations.exercise_15_1_1.*;

import static org.junit.Assert.*;

/**
 * Implemented to also support + and 0 prefixes
 */
public class ValidatorStrict implements Validator {
    public boolean checkInteger(String testInteger) {
        if (testInteger == null) return false;
        int length = testInteger.length();
        if (length < 2) return false;
        int pos = 0;
        char c = testInteger.charAt(0);
        if (c == '+') pos++;
        else if (c == '0') pos++;
        else if (c != '3') return false;
        if (pos == 1) {
            c = testInteger.charAt(pos);
            while(c == '0' && pos + 1 < length) {
                pos++;
                c = testInteger.charAt(pos);
            }
            if (c != '3') return false;
        }
        if (length - pos > 5) return false;
        for (int i = pos; i < length; i++) {
            if (!isDigit(testInteger.charAt(i))) return false;
        }
        return true;
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    public static void main(String... args) {
        Validator val = new ValidatorStrict();
        assertTrue(val.checkInteger("30"));
        assertTrue(val.checkInteger("030"));
        assertTrue(val.checkInteger("000030"));
        assertTrue(val.checkInteger("+30"));
        assertTrue(val.checkInteger("+030"));
        assertTrue(val.checkInteger("+000030"));
        assertTrue(val.checkInteger("+0000300"));
        assertTrue(val.checkInteger("+00003000"));
        assertTrue(val.checkInteger("+000030000"));
        assertFalse(val.checkInteger("+0000300000"));
        assertFalse(val.checkInteger("+00003000000"));
        assertFalse(val.checkInteger("+00000000"));
        assertFalse(val.checkInteger("+000000000000000000000000"));
        assertFalse(val.checkInteger("+000000000000000000000000123"));
    }
}
