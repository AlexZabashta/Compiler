package ast.node.misc;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ast.Node;
import ast.node.AbstractNode;

public class ErrorNode extends AbstractNode {

    public final List<Node> nodes = new ArrayList<Node>();

    @Override
    public void print(PrintWriter out) {
        out.println();
        out.print("! error nodes:");
        for (Node node : nodes) {
            out.print(' ');
            node.print(out);
        }
        out.println();
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println("! error nodes:");
        for (Node node : nodes) {
            printIndent(out, indent + 1);
            out.print("! ");
            node.print(out);
            out.println();
        }
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println("error");
        for (Node node : nodes) {
            node.printTree(out, indent + 1);
        }
    }

    @Override
    public String toString() {
        return "error";
    }

    @Override
    public boolean isRValue() {
        return false;
    }

    @Override
    public boolean isLValue() {
        return false;
    }

}
