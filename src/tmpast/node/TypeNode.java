package tmpast.node;

import java.io.PrintWriter;

import tmpast.type.Type;
import lex.Location;
import lex.token.Token;

public class TypeNode extends Token {

    public final Type type;

    public TypeNode(Type type, Location location) {
        super(location);
        this.type = type;
    }

    @Override
    public String toString() {
        return type + " at " + location;
    }

    @Override
    public void printToken(PrintWriter out) {
        out.print(type.toString());

    }

}
