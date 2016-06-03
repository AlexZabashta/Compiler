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

            for (RValue node : vars) {
                try {
                    Variable var = node.getVariable(z, e, log);
                    args.add(var);
                } catch (ParseException parseException) {
                    log.addException(parseException);
                }
            }

            Type type = function.type;
            Variable res = null;

            if (!type.idVoid()) {
                res = z.createVariable(type);
            }

            z.addAction(new CallFunction(res, function, args, null, fun.toString()));
        } catch (TypeMismatch | DeclarationException | UnexpectedVoidType exception) {
            log.addException(new SemanticException(exception, fun));
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
    public String toString() {
        return "call " + fun.toString();
    }

    @Override
    public Type type(Environment e) throws DeclarationException {
        return fun(e).type;
    }

    @Override
    public Variable getVariable(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Function function = fun(e);
            List<Variable> args = new ArrayList<Variable>();

            ParseException error = null;

            for (RValue node : vars) {
                try {
                    Variable var = node.getVariable(z, e, log);
                    args.add(var);
                } catch (ParseException parseException) {
                    log.addException(error = parseException);
                }
            }

            if (error != null) {
                throw error;
            }

            Type type = function.type;
            Variable res = z.createVariable(type);
            z.addAction(new CallFunction(res, function, args, null, fun.toString()));
            return res;
        } catch (TypeMismatch | DeclarationException | UnexpectedVoidType exception) {
            throw new SemanticException(exception, fun);
        }
    }

}
