package exception;

import lex.Token;

public class SemanticException extends ParseException {

    private static final long serialVersionUID = 1L;

    public SemanticException(String message, Token token) {
        super(message, token);
    }

    public SemanticException(Exception exception, Token token) {
        super(exception, token);
    }

}
