package ast.node.op;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.token.pure.Operator;
import misc.Characters;
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
        Type lt = left.type(e);
        Type rt = right.type(e);

        if (lt.idVoid()) {
            log.addException(new SemanticException("Unexpected void before operator", operator));
        }

        if (rt.idVoid()) {
            log.addException(new SemanticException("Unexpected void after operator", operator));
        }

        if (lt.idVoid() || rt.idVoid()) {
            return;
        }

        String funStr = Values.toString(funName(), lt, rt);

        if (e.getFunction(funStr, log, operator) != null) {
            left.action(z.subZone(false, operator.toString()), e, log);
            right.action(z.subZone(false, operator.toString()), e, log);
        }
    }

    public String funName() {
        switch (operator.string) {
        case "+":
            return "sys.add";
        case "-":
            return "sys.sub";
        case "*":
            return "sys.mul";
        case "/":
            return "sys.div";
        case "%":
            return "sys.mod";
        case "^":
            return "sys.xor";
        case "&":
            return "sys.and";
        case "&&":
            return "sys.partialAnd";
        case "|":
            return "sys.or";
        case "<":
            return "sys.less";
        case ">":
            return "sys.greater";
        case "<=":
            return "sys.lessOrEqual";
        case ">=":
            return "sys.greaterOrEqual";
        case "==":
            return "sys.equal";
        case "<>":
            return "sys.notEqual";
        default:
            throw new RuntimeException("Unknown operator " + operator);
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
    public Type type(Environment e) {
        try {
            char q = Characters.typeSeparator;
            String funStr = funName() + q + left.type(e) + q + right.type(e);
            Function function = e.f.get(funStr);
            return function.type;
        } catch (RuntimeException fake) {
            return right.type(e);
        }
    }

    @Override
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        Type lt = left.type(e);
        Type rt = right.type(e);

        if (lt.idVoid()) {
            log.addException(new SemanticException("Unexpected void before operator", operator));
        }

        if (rt.idVoid()) {
            log.addException(new SemanticException("Unexpected void after operator", operator));
        }

        if (lt.idVoid() || rt.idVoid()) {
            return;
        }

        VisibilityZone zone = z.subZone(false, operator.toString());
        Variable lvar = zone.createVariable(lt);
        left.getVariable(lvar, zone.subZone(false, null), e, log);

        Variable rvar = zone.createVariable(rt);
        right.getVariable(rvar, zone.subZone(false, null), e, log);

        List<Variable> args = new ArrayList<Variable>();
        args.add(lvar);
        args.add(rvar);

        String funStr = Values.toString(funName(), args);

        Function fun = e.getFunction(funStr, log, operator);

        if (Values.cmp(dst.type, fun.type, log, operator)) {
            zone.addAction(new CallFunction(dst, funStr, args, null, operator.toString()));
        }

    }
}
