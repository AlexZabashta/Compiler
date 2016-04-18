package lex.token.key_word;

import lex.Location;
import lex.Token;

public class ReturnToken extends Token {

    public ReturnToken(Location location) {
        super(location);
    }

    @Override
    public String toTokenString() {
        return "return";
    }

}
