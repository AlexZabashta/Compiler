package ast.node.op;

import java.io.PrintWriter;

import lex.token.pure.Operator;
import misc.EnumType;
import misc.Type;
import ast.SystemFunction;
import ast.node.AbstractNode;
import ast.node.RValue;
import code.Environment;
import code.VisibilityZone;
import code.act.Not;
import code.act.Size;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.TypeMismatch;
import exception.UnexpectedVoidType;

public class UOperatorNode extends AbstractNode implements RValue {

    public final RValue node;
    public final Operator operator;

    public UOperatorNode(RValue node, Operator operator) {
        this.node = node;
        this.operator = operator;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            type(e);
        } catch (DeclarationException exception) {
            log.addException(new SemanticException(exception, operator));
        }
        node.action(z, e, log);
    }

    @Override
    public void print(PrintWriter out) {
        out.print(operator.toTokenString());
        node.print(out);
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(operator);
        node.printTree(out, indent + 1);
    }

    public String funName() {
        switch (operator.string) {
        case "~":
            return SystemFunction.PAC + ".not";
        case "#":
            return SystemFunction.PAC + ".size";
        default:
            throw new RuntimeException("Unknown unary operator " + operator);
        }
    }

    @Override
    public String toString() {
        return operator.toString();
    }

    @Override
    public Type type(Environment e) throws DeclarationException {
        Type type = node.type(e);

        switch (operator.string) {
        case "~":
            if (type.equals(new Type(EnumType.INT)) || type.equals(new Type(EnumType.BOOL))) {
                return type;
            } else {
                throw new DeclarationException("Can't find ~" + type + " function");
            }
        case "#":
            return new Type(EnumType.INT);
        default:
            throw new RuntimeException("Unknown unary operator " + operator);
        }
    }

    @Override
    public Variable getVariable(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {

            Type type = type(e);
            if (operator.string == "~") {
                Variable var = node.getVariable(z, e, log);
                Variable res = z.createVariable(type);
                z.addAction(new Not(res, var, operator.toString()));
                return res;
            } else {
                Variable var = node.getVariable(z, e, log);
                Variable res = z.createVariable(type);
                z.addAction(new Size(res, var, operator.toString()));
                return res;
            }
        } catch (DeclarationException | UnexpectedVoidType | TypeMismatch exception) {
            throw new SemanticException(exception, operator);
        }
    }
}
