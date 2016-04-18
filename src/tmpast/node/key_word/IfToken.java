package tmpast.node.key_word;

import java.io.PrintWriter;

import lex.Location;
import lex.token.Token;

public class IfToken extends Token {

    public IfToken(Location location) {
        super(location);
    }

    @Override
    public void printToken(PrintWriter out) {
        out.print("if");
    }

}
