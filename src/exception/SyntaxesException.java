package exception;

import lex.Location;
import lex.Token;

public class SyntaxesException extends ParseException {

    private static final long serialVersionUID = 1L;

    public SyntaxesException(String message, char token, Location location) {
        this(message, Character.toString(token), location);
    }

    public SyntaxesException(String message, String token, Location location) {
        super(message, new ErrorToken(token, location));
    }

    public SyntaxesException(String message, Token token) {
        super(message, token);
    }

}
