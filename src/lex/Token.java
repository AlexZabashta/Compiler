package lex;

import java.io.PrintWriter;

public abstract class Token {

    public final Location location;

    public Token(Location location) {
        super();
        this.location = location;
    }

    public void print(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.print(toTokenString());
        out.print(' ');
        out.println(location);
    }

    public void printIndent(PrintWriter out, int indent) {
        while (--indent >= 0) {
            out.print("    ");
        }
    }

    @Override
    public String toString() {
        if (location == null) {
            return toTokenString();
        } else {
            return toTokenString() + " at " + location;
        }
    }

    public abstract String toTokenString();
}
