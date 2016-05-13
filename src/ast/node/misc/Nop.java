package ast.node.misc;

import java.io.PrintWriter;

import ast.node.AbstractNode;
import code.Environment;
import code.VisibilityZone;
import exception.Log;

public class Nop extends AbstractNode {

    @Override
    public void action(VisibilityZone z, Environment e, Log log) {
        z.addAction(new code.act.Nop());
    }

    @Override
    public void print(PrintWriter out) {
        out.print("?");
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println("NOP");
    }

    @Override
    public String toString() {
        return "nop";
    }

}
