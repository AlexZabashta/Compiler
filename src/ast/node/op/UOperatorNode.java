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
import code.Variable;
import code.VisibilityZone;
import code.act.CallFunction;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class UOperatorNode extends AbstractNode implements RValue {

    public final RValue node;
    public final Operator operator;

    public UOperatorNode(RValue node, Operator operator) {
        this.node = node;
        this.operator = operator;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        Type type = node.type(e);
        if (type.idVoid()) {
            log.addException(new SemanticException("Expected R-value", operator));
            return;
        }

        if (type.level != 0) {
            log.addException(new SemanticException("Expected not array R-value", operator));
            return;
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

    @Override
    public void rValue(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        Type type = node.type(e);
        if (type.idVoid()) {
            log.addException(new SemanticException("Expected R-value", operator));
            return;
        }

        if (type.level != 0) {
            log.addException(new SemanticException("Expected not array R-value", operator));
            return;
        }

        VisibilityZone zone = z.subZone(false, operator);
        Variable var = zone.createVariable(node.type(e));
        node.rValue(var, zone, e, log);

        List<Variable> args = new ArrayList<Variable>();
        args.add(var);

        String funStr = Values.toStringType("sys." + funName(), args);

        Function fun = e.f.get(funStr);

        if (fun == null) {
            log.addException(new SemanticException("Can't find " + funStr + " declaration", operator));
            return;
        }

        if (Values.cmp(dst.type, fun.type, log, operator)) {
            zone.addAction(new CallFunction(dst, funStr, args, null, operator));
        }

    }

    public String funName() {
        switch (operator.string) {
        case "~":
            return "not";
        case "#":
            return "size";
        default:
            throw new RuntimeException("Unknown unary operator " + operator);
        }
    }

    @Override
    public String toString() {
        return operator.toString();
    }

    @Override
    public Type type(Environment e) {
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
    public boolean isRValue() {
        return node.isRValue();
    }

    @Override
    public boolean isLValue() {
        return false;
    }

}
