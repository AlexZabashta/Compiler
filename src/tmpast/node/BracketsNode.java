package tmpast.node;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.Location;

public class BracketsNode extends Node {

    public final List<Node> nodes = new ArrayList<Node>();
    public final BracketsType type;
    public final Location location;

    public BracketsNode(BracketsType type, lex.Location location) {
        this.type = type;
        this.location = location;
    }

    @Override
    public void print(PrintWriter out, int indent) {
        printIndent(out, indent);

        out.print(BracketsType.open(type));
        out.print(' ');
        out.println(location);
        for (Node node : nodes) {
            node.print(out, indent + 1);
        }
        out.println(BracketsType.close(type));

    }

}
