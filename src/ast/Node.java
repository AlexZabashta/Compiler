package ast;

import java.io.PrintWriter;
import java.util.List;

import code.Environment;
import code.VisibilityZone;

public interface Node {

    public void action(VisibilityZone z, Environment e, List<String> errors);

    public void print(PrintWriter out);

    public void printIndent(PrintWriter out, int indent);

    public void println(PrintWriter out, int indent);

    public abstract void printTree(PrintWriter out, int indent);

}
