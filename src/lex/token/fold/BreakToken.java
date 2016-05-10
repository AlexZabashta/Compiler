package lex.token.fold;

import exception.SyntaxesException;
import lex.Location;
import lex.Token;

public class BreakToken extends Token {

    public final int level;

    public BreakToken(int level, Location location) throws SyntaxesException {
        super(location);
        if (level <= 0) {
            throw new SyntaxesException("(level = " + level + ") <= 0", this);
        }
        this.level = level;
    }

    @Override
    public String toTokenString() {
        if (level == 1) {
            return "break";
        } else {
            return "break." + level;
        }
    }

}
