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

public class UOperatorNode extends AbstractNode implements RValue {

    public final RValue node;
    public final Operator operator;

    public UOperatorNode(RValue node, Operator operator) {
        this.node = node;
        this.operator = operator;
    }

    @Override
    public void action(VisibilityZone z, Environment e, List<String> errors) {
        rValue(null, z, e, errors);
    }

    public void not(Variable dst, VisibilityZone z, Environment e, List<String> errors) {
        Type type = node.type(e);
        if (type.idVoid()) {
            errors.add("Can't take 'not' from void type " + operator);
            return;
        }

        if (type.level != 0) {
            errors.add("Can't take 'not' from array type " + operator);
            return;
        }

        if (dst == null || dst.type.idVoid()) {
            node.action(z, e, errors);
        } else {

            VisibilityZone zone = z.subZone(false, operator);
            Variable var = zone.createVariable(node.type(e));
            node.rValue(var, zone, e, errors);

            List<Variable> args = new ArrayList<Variable>();
            args.add(var);

            String funStr = Values.toStringType("sys.not", args);

            Function fun = e.f.get(funStr);

            if (fun == null) {
                errors.add("Can't find " + funStr + " at " + operator);
                return;
            }

            if (Values.cmp(dst.type, fun.type, errors, operator)) {
                zone.addAction(new CallFunction(dst, funStr, args, null, operator));
            }
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

    @Override
    public void rValue(Variable dst, VisibilityZone z, Environment e, List<String> errors) {
        switch (operator.string) {
        case "~": {
            not(dst, z, e, errors);
        }
            break;
        case "#": {
            size(dst, z, e, errors);
        }
            break;
        default:
            throw new RuntimeException("Unknown unary operator " + operator);
        }
    }

    public void size(Variable dst, VisibilityZone z, Environment e, List<String> errors) {
        Type type = node.type(e);
        if (type.idVoid()) {
            errors.add("Can't take size from void type " + operator);
            return;
        }

        if (dst == null || dst.type.idVoid()) {
            node.action(z, e, errors);
        } else {

            VisibilityZone zone = z.subZone(false, operator);
            Variable var = zone.createVariable(node.type(e));
            node.rValue(var, zone, e, errors);

            List<Variable> args = new ArrayList<Variable>();
            args.add(var);

            String funStr = Values.toStringType("sys.size", args);

            Function fun = e.f.get(funStr);

            if (fun == null) {
                errors.add("Can't find " + funStr + " at " + operator);
                return;
            }

            if (Values.cmp(dst.type, fun.type, errors, operator)) {
                zone.addAction(new CallFunction(dst, funStr, args, null, operator));
            }
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

}
