package lex.token.pure;

import lex.Location;
import lex.Token;

public class Number extends Token {

    public final int number;

    public Number(int number, Location location) {
        super(location);
        this.number = number;
    }

    @Override
    public String toTokenString() {
        return Integer.toString(number);
    }
}
