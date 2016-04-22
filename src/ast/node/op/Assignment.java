package ast.node.op;

import java.io.PrintWriter;
import java.util.List;

import lex.token.pure.Operator;
import misc.Type;
import ast.node.AbstractNode;
import ast.node.LValue;
import ast.node.RValue;
import code.Environment;
import code.Variable;
import code.VisibilityZone;

public class Assignment extends AbstractNode implements RValue {

    public final LValue left;
    public final Operator operator;

    public final RValue right;

    public Assignment(LValue left, RValue right, Operator operator) {
        if (operator.string != "=") {
            throw new RuntimeException("Expected = operator");
        }
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
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(operator);
        left.printTree(out, indent + 1);
        right.printTree(out, indent + 1);
    }

    @Override
    public void rValue(Variable var, VisibilityZone z, Environment e, List<String> errors) {
        VisibilityZone zone = z.subZone(false, operator);
        Type type = type(e);

        if (type.idVoid()) {
            errors.add("Can't assign void type at " + operator);
            return;
        }

        if (var == null || var.type.idVoid()) {
            var = zone.createVariable(type);
        }

        right.rValue(var, zone, e, errors);
        left.lValue(var, zone, e, errors);
    }

    @Override
    public String toString() {
        return operator.toString();
    }

    @Override
    public Type type(Environment e) {
        return right.type(e);
    }

}
