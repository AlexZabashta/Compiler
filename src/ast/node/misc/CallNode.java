package ast.node.misc;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.token.fold.VarToken;
import misc.Characters;
import misc.Type;
import ast.Function;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.Values;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.CallFunction;

public class CallNode extends AbstractNode implements RValue {

    public final VarToken fun;
    public final List<RValue> vars;

    public CallNode(VarToken fun, List<RValue> vars) {
        this.fun = fun;
        this.vars = vars;
    }

    @Override
    public void action(VisibilityZone z, Environment e, List<String> errors) {
        rValue(null, z, e, errors);
    }

    public String fun(Environment e) {
        StringBuilder builder = new StringBuilder();
        builder.append(fun.toTokenString());

        for (RValue node : vars) {
            builder.append(Characters.typeSeparator);
            builder.append(node.type(e));
        }

        return builder.toString();
    }

    @Override
    public void print(PrintWriter out) {
        out.print(fun.toTokenString());
        out.print('(');

        boolean sep = false;

        for (Node var : vars) {
            if (sep) {
                out.print(", ");
            }
            var.print(out);
            sep = true;
        }

        out.print(')');
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(fun);
        for (Node node : vars) {
            node.printTree(out, indent + 1);
        }
    }

    @Override
    public void rValue(Variable dst, VisibilityZone z, Environment e, List<String> errors) {
        String funStr = fun(e);
        Function function = e.f.get(funStr);

        if (function == null) {
            errors.add("Cant find " + funStr + " at " + fun);
            return;
        }
        List<Variable> args = new ArrayList<Variable>();

        VisibilityZone fz = z.subZone(false, fun);

        for (RValue node : vars) {
            VisibilityZone az = fz.subZone(false, fun);

            Variable var = fz.createVariable(node.type(e));

            node.rValue(var, az, e, errors);
            args.add(var);
        }

        if (dst == null || dst.type.idVoid()) {
            fz.addAction(new CallFunction(null, funStr, args, null, fun));
        } else {
            if (Values.cmp(dst.type, function.type, errors, fun)) {
                fz.addAction(new CallFunction(dst, funStr, args, null, fun));
            }
        }
    }

    @Override
    public String toString() {
        return "call " + fun.toString();
    }

    @Override
    public Type type(Environment e) {
        String fun = fun(e);
        Function function = e.f.get(fun);

        if (function == null) {
            throw new RuntimeException("Cant find " + fun);
        }

        return function.type;
    }
}
