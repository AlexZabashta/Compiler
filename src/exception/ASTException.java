package exception;

import lex.Token;

public class ASTException extends ParseException {

    private static final long serialVersionUID = 1L;

    public ASTException(String message, Token token) {
        super(message, token);
    }

}
