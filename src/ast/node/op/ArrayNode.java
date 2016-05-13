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
import code.Variable;
import code.VisibilityZone;
import code.act.CallFunction;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

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

        String funStr = Values.toString("get", arrayType, indexType);

        if (e.getFunction(funStr, log, token) != null) {
            array.action(z.subZone(false, token.toString()), e, log);
            index.action(z.subZone(false, token.toString()), e, log);
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
    public Type type(Environment e) {
        Type type = array.type(e);
        if (type.level == 0) {
            return new Type(EnumType.BOOL);
        } else {
            return new Type(type.type, type.level - 1);
        }
    }

    @Override
    public void setVariable(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException {
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

        VisibilityZone zone = z.subZone(false, null);
        Variable a = zone.createVariable(arrayType);
        array.getVariable(a, zone.subZone(false, null), e, log);
        Variable i = zone.createVariable(indexType);
        index.getVariable(i, zone.subZone(false, null), e, log);

        List<Variable> args = new ArrayList<Variable>();
        args.add(a);
        args.add(i);
        args.add(src);
        String funStr = Values.toString("set", args);

        Function fun = e.getFunction(funStr, log, token);

        if (fun != null) {
            zone.addAction(new CallFunction(null, funStr, args, null, token.toString()));
        }
    }

    @Override
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
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

        VisibilityZone zone = z.subZone(false, null);
        Variable a = zone.createVariable(arrayType);
        array.getVariable(a, zone.subZone(false, null), e, log);
        Variable i = zone.createVariable(indexType);
        index.getVariable(i, zone.subZone(false, null), e, log);

        List<Variable> args = new ArrayList<Variable>();
        args.add(a);
        args.add(i);
        String funStr = Values.toString("get", args);

        Function fun = e.getFunction(funStr, log, token);

        if (Values.cmp(dst.type, fun.type, log, token)) {
            zone.addAction(new CallFunction(dst, funStr, args, null, token.toString()));
        }
    }
}
