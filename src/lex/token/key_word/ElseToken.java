package lex.token.key_word;

import lex.Location;
import lex.Token;

public class ElseToken extends Token {

    public ElseToken(Location location) {
        super(location);
    }

    @Override
    public String toTokenString() {
        return "else";
    }

}
