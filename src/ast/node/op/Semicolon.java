package ast.node.op;

import java.io.PrintWriter;
import java.util.List;

import lex.token.pure.Operator;
import misc.Type;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.RValue;
import code.Environment;
import code.Variable;
import code.VisibilityZone;

public class Semicolon extends AbstractNode implements RValue {

    public final Node left, right;
    public final Operator operator;

    public Semicolon(Node left, Node right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public void action(VisibilityZone z, Environment e, List<String> errors) {
        rValue(null, z, e, errors);
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
    public void rValue(Variable var, VisibilityZone z, Environment e, List<String> errors) {
        VisibilityZone zone = z.subZone(false, operator);
        left.action(zone, e, errors);

        if (var == null || var.type.idVoid()) {
            right.action(zone, e, errors);
        } else {
            try {
                RValue rval = (RValue) right;
                Type type = rval.type(e);

                if (type.idVoid()) {
                    throw new ClassCastException();
                }
                rval.rValue(var, zone, e, errors);
            } catch (ClassCastException fake) {
                errors.add("Can't convert void type to " + var.type + "at " + operator);
            }

        }

    }

    @Override
    public String toString() {
        return operator.toString();
    }

    @Override
    public Type type(Environment e) {
        try {
            return ((RValue) right).type(e);
        } catch (ClassCastException fake) {
            return new Type();
        }
    }

}
