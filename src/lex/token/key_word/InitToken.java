package lex.token.key_word;

import lex.Location;
import lex.Token;

public class InitToken extends Token {

    public InitToken(Location location) {
        super(location);
    }

    @Override
    public String toTokenString() {
        return "init";
    }

}
