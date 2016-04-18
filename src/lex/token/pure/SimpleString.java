package lex.token.pure;

import lex.Location;
import lex.Token;

public class SimpleString extends Token {

    public final String string;

    public SimpleString(String string, Location location) {
        super(location);
        this.string = string.toLowerCase().intern();
    }

    @Override
    public String toTokenString() {
        return string;
    }
}
