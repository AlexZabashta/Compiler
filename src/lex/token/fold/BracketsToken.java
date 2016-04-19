package lex.token.fold;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.Token;

public class BracketsToken extends Token {

    public final List<Token> tokens = new ArrayList<Token>();
    public final BracketsType type;

    public BracketsToken(BracketsType type, lex.Location location) {
        super(location);
        this.type = type;
    }

    @Override
    public void print(PrintWriter out, int indent) {
        printIndent(out, indent);

        out.print(BracketsType.open(type));
        out.print(' ');
        out.println(location);
        for (Token token : tokens) {
            token.print(out, indent + 1);
        }
        printIndent(out, indent);
        out.println(BracketsType.close(type));

    }

    @Override
    public String toTokenString() {
        return BracketsType.open(type) + BracketsType.close(type);
    }

}
