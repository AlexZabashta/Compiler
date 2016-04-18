package lex.token.key_word;

import lex.Location;
import lex.Token;

public class IfToken extends Token {

    public IfToken(Location location) {
        super(location);
    }

    @Override
    public String toTokenString() {
        return "if";
    }

}
