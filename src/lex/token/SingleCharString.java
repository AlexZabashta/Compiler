package lex.token;

import java.io.PrintWriter;

import lex.Location;
import misc.Characters;

public class SingleCharString extends Token {

    public final char symbol;

    public SingleCharString(char symbol, Location location) {
        super(location);
        this.symbol = symbol;
    }

    @Override
    public void printToken(PrintWriter out) {
        out.print('\'');
        out.print(Characters.escape(symbol));
        out.print('\'');
    }

    @Override
    public String toString() {
        return "\'" + Characters.escape(symbol) + "\' at " + location;
    }
}
