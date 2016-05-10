package exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Log {
    private final List<ParseException> exceptions = new ArrayList<ParseException>();
    public final boolean enableLogging;

    public Log(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }

    public void addException(ParseException exception) throws ParseException {
        if (enableLogging) {
            exceptions.add(exception);
        } else {
            throw exception;
        }
    }

    public boolean printErrorsAndClear(String message) {
        if (!enableLogging) {
            throw new RuntimeException("Logging disabled");
        }
        if (exceptions.isEmpty()) {
            return false;
        }
        System.err.println("    " + message);

        Collections.sort(exceptions);

        for (ParseException exception : exceptions) {
            System.err.println("    " + "    " + exception.getMessage());
        }
        exceptions.clear();
        return true;
    }

}
