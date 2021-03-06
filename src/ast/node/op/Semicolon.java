package ast.node.op;

import java.io.PrintWriter;

import lex.token.pure.Operator;
import misc.Type;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.RValue;
import code.Environment;
import code.VisibilityZone;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class Semicolon extends AbstractNode implements RValue {

    public final Node left, right;
    public final Operator operator;

    public Semicolon(Node left, Node right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        left.action(z, e, log);
        right.action(z, e, log);
    }

    @Override
    public void print(PrintWriter out) {
        left.print(out);
        out.print(' ');
        out.print(operator.toTokenString());
        out.print(' ');
        right.print(out);
    }

    @Override
    public void println(PrintWriter out, int indent) {
        left.println(out, indent);
        out.println(';');
        right.println(out, indent);
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(operator);
        left.printTree(out, indent + 1);
        right.printTree(out, indent + 1);
    }

    @Override
    public String toString() {
        return operator.toString();
    }

    @Override
    public Type type(Environment e) throws DeclarationException {
        try {
            return ((RValue) right).type(e);
        } catch (ClassCastException fake) {
            return new Type();
        }
    }

    @Override
    public Variable getVariable(VisibilityZone z, Environment e, Log log) throws ParseException {
        left.action(z, e, log);

        try {
            RValue rval = (RValue) right;
            return rval.getVariable(z, e, log);
        } catch (ClassCastException fake) {
            throw new SemanticException("Expected R-Value after", operator);
        }
    }

}
