package lex.token;

import java.io.PrintWriter;

import lex.Location;
import misc.Characters;

public class SimpleString extends Token {

    public final String string;

    public SimpleString(String string, Location location) {
        super(location);
        this.string = string.toLowerCase().intern();
    }

    @Override
    public void printToken(PrintWriter out) {
        out.print(string);
    }

    @Override
    public String toString() {
        return string + " at " + location;
    }
}
