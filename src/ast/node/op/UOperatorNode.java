package ast.node.op;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.token.pure.Operator;
import misc.EnumType;
import misc.Type;
import ast.Function;
import ast.SystemFunction;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.Values;
import code.Environment;
import code.VisibilityZone;
import code.act.CallFunction;
import code.act.Not;
import code.act.Size;
import code.var.LocalVariable;
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
            log.addException(new SemanticException(exception.getMessage(), operator));
        }
        node.action(z.subZone(false, operator.toString()), e, log);
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
    public void getLocalVariable(LocalVariable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        try {

            Type type = type(e);
            if (operator.string == "~") {
                VisibilityZone zone = z.subZone(false, null);
                LocalVariable var = zone.createVariable(type);
                node.getLocalVariable(var, zone, e, log);
                z.addAction(new Not(dst, var, operator.toString()));
            } else {
                Type arrayType = node.type(e);
                VisibilityZone zone = z.subZone(false, null);

                LocalVariable var = zone.createVariable(arrayType);
                node.getLocalVariable(var, zone, e, log);
                z.addAction(new Size(dst, var, operator.toString()));
            }
        } catch (DeclarationException | UnexpectedVoidType | TypeMismatch exception) {
            log.addException(new SemanticException(exception.getMessage(), operator));
        }

    }
}
