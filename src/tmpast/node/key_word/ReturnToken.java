package tmpast.node.key_word;

import java.io.PrintWriter;

import lex.Location;
import lex.token.Token;

public class ReturnToken extends Token {

    public ReturnToken(Location location) {
        super(location);
    }

    @Override
    public void printToken(PrintWriter out) {
        out.print("return");
    }

}
