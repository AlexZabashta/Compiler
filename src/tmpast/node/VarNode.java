package tmpast.node;

import java.io.PrintWriter;

import tmpast.type.Type;
import lex.Location;
import lex.token.SimpleString;
import lex.token.Token;

public class VarNode extends Node {

    public final SimpleString pac, name;

    public VarNode(SimpleString pac, SimpleString name) {
        this.pac = pac;
        this.name = name;
    }

    @Override
    public String toString() {
        if (pac == null) {
            return name.toString();
        } else {
            return pac + "." + name;
        }

    }

    @Override
    public void print(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println("var");
        if (pac == null) {
            name.print(out, indent + 1);
        } else {
            pac.print(out, indent + 1);
            name.print(out, indent + 1);
        }
    }

}
