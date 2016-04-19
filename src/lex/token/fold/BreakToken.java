package lex.token.fold;

import lex.Location;
import lex.Token;

public class BreakToken extends Token {

    final int level;

    public BreakToken(int level, Location location) {
        super(location);
        if (level <= 0) {
            throw new RuntimeException("(level = " + level + ") <= 0");
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
