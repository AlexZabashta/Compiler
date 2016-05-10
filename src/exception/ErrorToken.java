package exception;

import lex.Location;
import lex.token.pure.Comment;

public class ErrorToken extends Comment {

    public ErrorToken(String string, Location location) {
        super(string, location);
    }

    @Override
    public String toTokenString() {
        return string;
    }
}
