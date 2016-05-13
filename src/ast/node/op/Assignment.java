package ast.node.op;

import java.io.PrintWriter;

import lex.token.pure.Operator;
import misc.Type;
import ast.node.AbstractNode;
import ast.node.LValue;
import ast.node.RValue;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

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
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(false, operator.toString());
        Type type = type(e);

        if (type.idVoid()) {
            log.addException(new SemanticException("Can't assign void type", operator));
            return;
        }

        Variable var = zone.createVariable(type);
        right.getVariable(var, zone, e, log);
        left.setVariable(var, zone, e, log);
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
    public String toString() {
        return operator.toString();
    }

    @Override
    public Type type(Environment e) {
        return right.type(e);
    }

    @Override
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        right.getVariable(dst, z.subZone(false, operator.toString()), e, log);
        left.setVariable(dst, z.subZone(false, operator.toString()), e, log);
    }

}
