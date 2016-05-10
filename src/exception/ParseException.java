package exception;

import java.util.Objects;

import lex.Token;

public class ParseException extends Exception implements Comparable<ParseException> {
    private static final long serialVersionUID = 1L;

    public final Token token;
    public final String message;

    public ParseException(String message, Token token) {
        super(message);
        this.message = Objects.requireNonNull(message);
        this.token = token;
    }

    @Override
    public int compareTo(ParseException pe) {

        boolean x = token == null, y = pe.token == null;

        int cmp;

        if ((cmp = Boolean.compare(x, y)) != 0) {
            return cmp;
        }

        if (!x && !y && (cmp = token.compareTo(pe.token)) != 0) {
            return cmp;
        }

        return message.compareTo(pe.message);
    }
    
    // Syntaxes

    @Override
    public String getMessage() {
        if (token == null) {
            return message;
        } else {
            return message + " at " + token;
        }
    }

}
