package ast.node.op;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.Token;
import misc.EnumType;
import misc.Type;
import ast.Function;
import ast.node.AbstractNode;
import ast.node.LValue;
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
import exception.TypeInitException;
import exception.TypeMismatch;
import exception.UnexpectedVoidType;

public class ArrayNode extends AbstractNode implements LValue, RValue {

    public final RValue array, index;
    public final Token token;

    public ArrayNode(RValue array, RValue index, Token token) {
        this.array = array;
        this.index = index;
        this.token = token;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Type arrayType = array.type(e);

            if (arrayType.idVoid()) {
                log.addException(new SemanticException("Can't cast void to array", token));
                return;
            }

            Type indexType = index.type(e);

            if (indexType.idVoid()) {
                log.addException(new SemanticException("Can't cast void to index", token));
                return;
            }

            String funStr = Values.toString("sys.get", arrayType, indexType);
            e.function(funStr);
            array.action(z, e, log);
            index.action(z, e, log);
        } catch (DeclarationException exception) {
            log.addException(new SemanticException(exception, token));
        }
    }

    @Override
    public void print(PrintWriter out) {
        array.print(out);
        out.print('[');
        index.print(out);
        out.print(']');
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(token);
        array.printTree(out, indent + 1);
        index.printTree(out, indent + 1);
    }

    @Override
    public String toString() {
        return token.toString();
    }

    @Override
    public Type type(Environment e) throws DeclarationException {
        Type type = array.type(e);
        if (type.dim == 0) {
            return new Type(EnumType.BOOL);
        } else {
            try {
                return new Type(type.type, type.dim - 1);
            } catch (TypeInitException neverHappen) {
                throw new RuntimeException(neverHappen);
            }
        }
    }

    @Override
    public void setVariable(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Type arrayType = array.type(e);

            if (arrayType.idVoid()) {
                throw new UnexpectedVoidType("Can't cast void to array");
            }

            Type indexType = index.type(e);

            if (indexType.idVoid()) {
                throw new UnexpectedVoidType("Can't cast void to index");
            }
            List<Variable> args = new ArrayList<Variable>();

            try {
                Variable a = array.getVariable(z, e, log);
                args.add(a);
            } catch (ParseException exception) {
                log.addException(exception);
            }

            try {
                Variable i = index.getVariable(z, e, log);
                args.add(i);
            } catch (ParseException exception) {
                log.addException(exception);
            }

            args.add(src);

            String funStr = Values.toString("sys.set", args);

            Function fun = e.function(funStr);

            z.addAction(new CallFunction(null, fun, args, null, token.toString()));

        } catch (DeclarationException | UnexpectedVoidType | TypeMismatch exception) {
            log.addException(new SemanticException(exception, token));
        }

    }

    @Override
    public Variable getVariable(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Type arrayType = array.type(e);

            if (arrayType.idVoid()) {
                throw new UnexpectedVoidType("Can't cast void to array");
            }

            Type indexType = index.type(e);

            if (indexType.idVoid()) {
                throw new UnexpectedVoidType("Can't cast void to index");
            }
            Variable res = z.createVariable(type(e));
            List<Variable> args = new ArrayList<Variable>();

            try {
                Variable a = array.getVariable(z, e, log);
                args.add(a);
            } catch (ParseException exception) {
                log.addException(exception);
            }

            try {
                Variable i = index.getVariable(z, e, log);
                args.add(i);
            } catch (ParseException exception) {
                log.addException(exception);
            }

            String funStr = Values.toString("sys.get", args);

            Function fun = e.function(funStr);

            z.addAction(new CallFunction(res, fun, args, null, token.toString()));
            return res;
        } catch (DeclarationException | UnexpectedVoidType | TypeMismatch exception) {
            throw new SemanticException(exception, token);
        }
    }
}
