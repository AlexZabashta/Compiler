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
import code.act.Add;
import code.act.And;
import code.act.CallFunction;
import code.act.Or;
import code.act.Sub;
import code.act.Xor;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.TypeMismatch;
import exception.UnexpectedVoidType;

public class BOperatorNode extends AbstractNode implements RValue {

    public final RValue left, right;
    public final Operator operator;

    public BOperatorNode(RValue left, RValue right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            type(e);
        } catch (DeclarationException exception) {
            log.addException(new SemanticException(exception.getMessage(), operator));
        }

        left.action(z.subZone(false, operator.toString()), e, log);
        right.action(z.subZone(false, operator.toString()), e, log);
    }

    void add(Variable res, Variable a, Variable b, VisibilityZone z) throws TypeMismatch {
        z.addAction(new Add(res, a, b, operator.toString()));
    }

    Type addType(Type x, Type y) {
        if (operator.string != "+") {
            return null;
        }
        if (x.equals(new Type(EnumType.INT)) && y.equals(new Type(EnumType.INT))) {
            return new Type(EnumType.INT);
        }
        return null;
    }

    void and(Variable res, Variable a, Variable b, VisibilityZone z) throws TypeMismatch {
        z.addAction(new And(res, a, b, operator.toString()));
    }

    Type andType(Type x, Type y) {
        if (operator.string != "&") {
            return null;
        }
        if (x.equals(new Type(EnumType.INT)) && y.equals(new Type(EnumType.INT))) {
            return new Type(EnumType.INT);
        }

        if (x.equals(new Type(EnumType.BOOL)) && y.equals(new Type(EnumType.BOOL))) {
            return new Type(EnumType.BOOL);
        }
        return null;
    }

    public String fullfunName() {
        return SystemFunction.PAC + '.' + funName();
    }

    public String funName() {
        switch (operator.string) {
        case "+":
            return "add";
        case "-":
            return "sub";
        case "*":
            return "mul";
        case "/":
            return "div";
        case "%":
            return "mod";
        case "^":
            return "xor";
        case "&":
            return "and";
        case "&&":
            return "partialand";
        case "||":
            return "partialor";
        case "|":
            return "or";
        case "<":
            return "less";
        case ">":
            return "greater";
        case "<=":
            return "lessorequal";
        case ">=":
            return "greaterorequal";
        case "==":
            return "equal";
        case "<>":
            return "notequal";
        default:
            throw new RuntimeException("Unknown operator " + operator);
        }
    }

    @Override
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Type lt = left.type(e);
            Type rt = right.type(e);

            if (lt.idVoid()) {
                throw new UnexpectedVoidType("Unexpected void before operator");
            }

            if (rt.idVoid()) {
                throw new UnexpectedVoidType("Unexpected void after operator");
            }

            VisibilityZone zone = z.subZone(false, operator.toString());
            Variable lvar = zone.createVariable(lt);
            left.getVariable(lvar, zone.subZone(false, null), e, log);

            Variable rvar = zone.createVariable(rt);
            right.getVariable(rvar, zone.subZone(false, null), e, log);

            if ((addType(lt, rt)) != null) {
                add(dst, lvar, rvar, zone);
                return;
            }
            if ((subType(lt, rt)) != null) {
                sub(dst, lvar, rvar, zone);
                return;
            }
            if ((andType(lt, rt)) != null) {
                and(dst, lvar, rvar, zone);
                return;
            }
            if ((xorType(lt, rt)) != null) {
                xor(dst, lvar, rvar, zone);
                return;
            }
            if ((orType(lt, rt)) != null) {
                or(dst, lvar, rvar, zone);
                return;
            }

            List<Variable> args = new ArrayList<Variable>();
            args.add(lvar);
            args.add(rvar);

            String funStr = Values.toString(fullfunName(), args);

            Function function = e.function(funStr);

            zone.addAction(new CallFunction(dst, function, args, null, operator.toString()));
        } catch (DeclarationException | UnexpectedVoidType | TypeMismatch exception) {
            log.addException(new SemanticException(exception.getMessage(), operator));
        }
    }

    void or(Variable res, Variable a, Variable b, VisibilityZone z) throws TypeMismatch {
        z.addAction(new Or(res, a, b, operator.toString()));
    }

    Type orType(Type x, Type y) {
        if (operator.string != "|") {
            return null;
        }
        if (x.equals(new Type(EnumType.INT)) && y.equals(new Type(EnumType.INT))) {
            return new Type(EnumType.INT);
        }

        if (x.equals(new Type(EnumType.BOOL)) && y.equals(new Type(EnumType.BOOL))) {
            return new Type(EnumType.BOOL);
        }
        return null;
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

    void sub(Variable res, Variable a, Variable b, VisibilityZone z) throws TypeMismatch {
        z.addAction(new Sub(res, a, b, operator.toString()));
    }

    Type subType(Type x, Type y) {
        if (operator.string != "-") {
            return null;
        }
        if (x.equals(new Type(EnumType.INT)) && y.equals(new Type(EnumType.INT))) {
            return new Type(EnumType.INT);
        }
        return null;
    }

    @Override
    public String toString() {
        return operator.toString();
    }

    @Override
    public Type type(Environment e) throws DeclarationException {
        Type lType = left.type(e), rType = right.type(e);

        Type ret;

        if ((ret = addType(lType, rType)) != null) {
            return ret;
        }
        if ((ret = subType(lType, rType)) != null) {
            return ret;
        }
        if ((ret = andType(lType, rType)) != null) {
            return ret;
        }
        if ((ret = xorType(lType, rType)) != null) {
            return ret;
        }
        if ((ret = orType(lType, rType)) != null) {
            return ret;
        }

        String funStr = Values.toString(fullfunName(), lType, rType);
        return e.function(funStr).type;
    }

    void xor(Variable res, Variable a, Variable b, VisibilityZone z) throws TypeMismatch {
        z.addAction(new Xor(res, a, b, operator.toString()));
    }

    Type xorType(Type x, Type y) {
        if (operator.string != "^") {
            return null;
        }
        if (x.equals(new Type(EnumType.INT)) && y.equals(new Type(EnumType.INT))) {
            return new Type(EnumType.INT);
        }

        if (x.equals(new Type(EnumType.BOOL)) && y.equals(new Type(EnumType.BOOL))) {
            return new Type(EnumType.BOOL);
        }
        return null;
    }
}
