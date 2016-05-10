package ast.node.op;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.Token;
import misc.EnumType;
import misc.Type;
import ast.Function;
import ast.node.AbstractNode;
import ast.node.LRValue;
import ast.node.RValue;
import ast.node.Values;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.CallFunction;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class ArrayNode extends AbstractNode implements LRValue {

    public final RValue array, index;
    public final Token token;

    public ArrayNode(RValue array, RValue index, Token token) {
        this.array = array;
        this.index = index;
        this.token = token;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(false, token);

        Type arrayType = array.type(e);

        if (arrayType.idVoid()) {
            log.addException(new SemanticException("Can't cast void to array", token));
            return;
        }

        Variable a = zone.createVariable(arrayType);
        array.rValue(a, zone, e, log);
        Variable i = zone.createVariable(new Type(EnumType.INT));
        index.rValue(i, zone, e, log);
    }

    @Override
    public void lValue(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(false, token);

        Type arrayType = array.type(e);

        if (arrayType.idVoid()) {
            log.addException(new SemanticException("Can't cast void to array", token));
            return;
        }

        Variable a = zone.createVariable(arrayType);
        array.rValue(a, zone, e, log);
        Variable i = zone.createVariable(new Type(EnumType.INT));
        index.rValue(i, zone, e, log);

        List<Variable> args = new ArrayList<Variable>();
        args.add(a);
        args.add(i);
        args.add(src);
        String funStr = Values.toStringType("set", args);
        Function fun = e.f.get(funStr);

        if (fun == null) {
            log.addException(new SemanticException("Can't find " + funStr, token));
            return;
        }

        if (arrayType.level == 0) {
            if (Values.cmp(new Type(EnumType.BOOL), src.type, log, token)) {
                zone.addAction(new CallFunction(null, funStr, args, null, token));
            }
        } else {
            Type type = new Type(arrayType.type, arrayType.level - 1);
            if (Values.cmp(type, src.type, log, token)) {
                zone.addAction(new CallFunction(null, funStr, args, null, token));
            }
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
    public void rValue(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(false, token);

        Type arrayType = array.type(e);

        if (arrayType.idVoid()) {
            log.addException(new SemanticException("Can't cast void to array", token));
            return;
        }

        Variable a = zone.createVariable(arrayType);
        array.rValue(a, zone, e, log);
        Variable i = zone.createVariable(new Type(EnumType.INT));
        index.rValue(i, zone, e, log);

        List<Variable> args = new ArrayList<Variable>();
        args.add(a);
        args.add(i);
        String funStr = Values.toStringType("get", args);

        Function fun = e.f.get(funStr);

        if (fun == null) {
            log.addException(new SemanticException("Can't find " + funStr, token));
            return;
        }

        if (Values.cmp(dst.type, fun.type, log, token)) {
            zone.addAction(new CallFunction(dst, funStr, args, null, token));
        }
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
    public boolean isRValue() {
        return array.isRValue();
    }

    @Override
    public boolean isLValue() {
        return array.isLValue();
    }

}
