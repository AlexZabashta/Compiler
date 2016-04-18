package tmpast.node.key_word;

import java.io.PrintWriter;

import lex.Location;
import lex.token.Token;

public class ElseToken extends Token {

    public ElseToken(Location location) {
        super(location);
    }

    @Override
    public void printToken(PrintWriter out) {
        out.print("else");
    }

}
