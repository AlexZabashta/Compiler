package lex.token.pure;

import lex.Location;
import lex.Token;
import misc.Characters;

public class SingleCharString extends Token {

    public final char symbol;

    public SingleCharString(char symbol, Location location) {
        super(location);
        this.symbol = symbol;
    }

    @Override
    public String toTokenString() {
        return "\'" + Characters.escape(symbol) + "\'";
    }
}
