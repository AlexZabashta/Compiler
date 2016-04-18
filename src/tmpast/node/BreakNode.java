package tmpast.node;

import java.io.PrintWriter;

import lex.Location;

public class BreakNode extends Node {

    final int level;
    final Location location;

    public BreakNode(int level, Location location) {
        if (level <= 0) {
            throw new RuntimeException("(level = " + level + ") <= 0");
        }
        this.level = level;
        this.location = location;
    }

    @Override
    public void print(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.print("break ");
        out.print(level);
        out.print(' ');
        out.println(location);
    }

}
