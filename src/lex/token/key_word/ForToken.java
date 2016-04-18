package lex.token.key_word;

import lex.Location;
import lex.Token;

public class ForToken extends Token {

    public ForToken(Location location) {
        super(location);
    }

    @Override
    public String toTokenString() {
        return "for";
    }

}
