package xj_conc.ch15_optimizations.solution_15_3_1;

import xj_conc.ch15_optimizations.exercise_15_1_1.*;

public class ValidatorImplSwitchReorder implements Validator {
    public final boolean checkInteger(String testInteger) {
        if (testInteger == null)
            return false;
        int length = testInteger.length();

        if (length < 2)
            return false;
        if (length > 5)
            return false;

        if (testInteger.charAt(0) != '3')
            return false;

        char temp;
        switch (length) {
            case 5:
                if ((temp = testInteger.charAt(4)) < '0' || temp > '9')
                    return false;
            case 4:
                if ((temp = testInteger.charAt(3)) < '0' || temp > '9')
                    return false;
            case 3:
                if ((temp = testInteger.charAt(2)) < '0' || temp > '9')
                    return false;
            case 2:
                if ((temp = testInteger.charAt(1)) < '0' || temp > '9')
                    return false;
        }

        return true;
    }
}
