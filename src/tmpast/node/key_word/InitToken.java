package tmpast.node.key_word;

import java.io.PrintWriter;

import lex.Location;
import lex.token.Token;

public class InitToken extends Token {

    public InitToken(Location location) {
        super(location);
    }

    @Override
    public void printToken(PrintWriter out) {
        out.print("init");
    }

}
