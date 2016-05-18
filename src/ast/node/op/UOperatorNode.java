package ast.node.op;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.token.pure.Operator;
import misc.EnumType;
import misc.Type;
import ast.Function;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.Values;
import code.Environment;
import code.VisibilityZone;
import code.act.CallFunction;
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
            Type type = node.type(e);
            if (type.idVoid()) {
                throw new UnexpectedVoidType("Expected R-value");
            }

            if (type.dim != 0) {
                throw new UnexpectedVoidType("Expected not array R-value");
            }

            String funStr = Values.toString(funName(), type);
            e.function(funStr);
            node.action(z.subZone(false, operator.toString()), e, log);
        } catch (DeclarationException | UnexpectedVoidType exception) {
            log.addException(new SemanticException(exception.getMessage(), operator));
        }
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
            return "sys.not";
        case "#":
            return "sys.size";
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
        switch (operator.string) {
        case "~":
            return node.type(e);
        case "#":
            return new Type(EnumType.INT);
        default:
            throw new RuntimeException("Unknown unary operator " + operator);
        }

    }

    @Override
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Type type = node.type(e);
            if (type.idVoid()) {
                throw new UnexpectedVoidType("Expected R-value");
            }

            if (type.dim != 0) {
                throw new UnexpectedVoidType("Expected not array R-value");
            }

            VisibilityZone zone = z.subZone(false, operator.toString());
            Variable var = zone.createVariable(type);
            List<Variable> args = new ArrayList<Variable>();
            args.add(var);

            String funStr = Values.toString(funName(), args);

            Function fun = e.function(funStr);

            zone.addAction(new CallFunction(dst, fun, args, null, operator.toString()));
        } catch (DeclarationException | UnexpectedVoidType | TypeMismatch exception) {
            log.addException(new SemanticException(exception.getMessage(), operator));
        }

    }
}
