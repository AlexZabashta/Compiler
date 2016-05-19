package ast.node.op;

import java.io.PrintWriter;

import lex.token.pure.Operator;
import misc.Type;
import ast.node.AbstractNode;
import ast.node.LValue;
import ast.node.RValue;
import code.Environment;
import code.VisibilityZone;
import code.var.LocalVariable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.UnexpectedVoidType;

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
        try {
            VisibilityZone zone = z.subZone(false, operator.toString());
            Type type = type(e);

            LocalVariable var = zone.createVariable(type);
            right.getLocalVariable(var, zone, e, log);
            left.setLocalVariable(var, zone, e, log);
        } catch (UnexpectedVoidType | DeclarationException exception) {
            log.addException(new SemanticException(exception.getMessage(), operator));
        }
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
    public Type type(Environment e) throws DeclarationException {
        return right.type(e);
    }

    @Override
    public void getLocalVariable(LocalVariable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        right.getLocalVariable(dst, z.subZone(false, operator.toString()), e, log);
        left.setLocalVariable(dst, z.subZone(false, operator.toString()), e, log);
    }

}
