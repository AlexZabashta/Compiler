package lex;

import java.io.PrintWriter;

public abstract class Token implements Comparable<Token> {

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
    public int compareTo(Token token) {
        boolean x = location == null, y = token.location == null;

        int cmp;

        if ((cmp = Boolean.compare(x, y)) != 0) {
            return cmp;
        }

        if (!x && !y && (cmp = location.compareTo(token.location)) != 0) {
            return cmp;
        }

        return toTokenString().compareTo(token.toTokenString());
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
