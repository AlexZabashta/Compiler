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

public class CallNode extends AbstractNode implements RValue {

    public final VarToken fun;
    public final List<RValue> vars;

    public CallNode(VarToken fun, List<RValue> vars) {
        this.fun = fun;
        this.vars = vars;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Function function = fun(e);
            List<Variable> args = new ArrayList<Variable>();

            VisibilityZone fz = z.subZone(false, fun.toString());

            for (RValue node : vars) {
                VisibilityZone az = fz.subZone(false, fun.toString());

                Variable var = fz.createVariable(node.type(e));

                node.getVariable(var, az, e, log);
                args.add(var);
            }

            Type type = function.type;
            Variable res = null;

            if (!type.idVoid()) {
                res = fz.createVariable(type);
            }

            fz.addAction(new CallFunction(res, function, args, null, fun.toString()));
        } catch (TypeMismatch | DeclarationException | UnexpectedVoidType exception) {
            log.addException(new SemanticException(exception.getMessage(), fun));
        }
    }

    public Function fun(Environment e) throws DeclarationException {
        StringBuilder builder = new StringBuilder();
        builder.append(fun.toTokenString());

        for (RValue node : vars) {
            builder.append(Characters.typeSeparator);
            builder.append(node.type(e));
        }

        return e.function(builder.toString());
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
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Function function = fun(e);
            List<Variable> args = new ArrayList<Variable>();
            VisibilityZone fz = z.subZone(false, fun.toString());

            for (RValue node : vars) {
                VisibilityZone az = fz.subZone(false, fun.toString());

                Variable var;
                var = fz.createVariable(node.type(e));

                node.getVariable(var, az, e, log);
                args.add(var);
            }

            fz.addAction(new CallFunction(dst, function, args, null, fun.toString()));

        } catch (UnexpectedVoidType | DeclarationException | TypeMismatch exception) {
            log.addException(new SemanticException(exception.getMessage(), fun));
        }
    }

    @Override
    public String toString() {
        return "call " + fun.toString();
    }

    @Override
    public Type type(Environment e) throws DeclarationException {
        return fun(e).type;
    }

}
