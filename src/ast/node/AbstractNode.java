package ast.node;

import java.io.PrintWriter;

import ast.Node;
import code.Environment;
import code.VisibilityZone;
import exception.Log;
import exception.ParseException;

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
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
    }

}
