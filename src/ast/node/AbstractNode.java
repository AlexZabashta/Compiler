package ast.node;

import java.io.PrintWriter;
import java.util.List;

import ast.Node;
import code.Environment;
import code.VisibilityZone;

public abstract class AbstractNode implements Node {

    @Override
    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        print(out);
    }

    @Override
    public void printIndent(PrintWriter out, int indent) {
        while (--indent >= 0) {
            out.print("    ");
        }
    }

    @Override
    public void action(VisibilityZone z, Environment e, List<String> errors) {
    }

}
