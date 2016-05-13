package ast;

import java.io.PrintWriter;

import code.Environment;
import code.VisibilityZone;
import exception.Log;
import exception.ParseException;

public interface Node {

    void action(VisibilityZone z, Environment e, Log log) throws ParseException;

    void print(PrintWriter out);

    void printIndent(PrintWriter out, int indent);

    void println(PrintWriter out, int indent);

    void printTree(PrintWriter out, int indent);

}
