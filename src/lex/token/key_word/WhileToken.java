package lex.token.key_word;

import lex.Location;
import lex.Token;

public class WhileToken extends Token {

    public WhileToken(Location location) {
        super(location);
    }

    @Override
    public String toTokenString() {
        return "while";
    }

}
