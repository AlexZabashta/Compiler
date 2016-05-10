package ast.node.op;

import java.io.PrintWriter;

import lex.Token;
import misc.Type;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.LRValue;
import ast.node.LValue;
import ast.node.RValue;
import ast.node.misc.Nop;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class FBracketsNode extends AbstractNode implements LRValue {

    public final Node node;
    public final Token token;

    public FBracketsNode(Node node, Token token) {
        this.node = node;
        this.token = token;
    }

    public FBracketsNode(Token token) {
        this.node = new Nop();
        this.token = token;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(true, token);
        node.action(zone, e, log);
        zone.removeAll(e.lv);
    }

    @Override
    public void print(PrintWriter out) {
        out.print('{');
        node.print(out);
        out.print('}');
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println('{');
        node.println(out, indent + 1);
        out.println();
        printIndent(out, indent);
        out.print('}');
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(token);
        node.printTree(out, indent + 1);
    }

    @Override
    public void rValue(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(true, token);

        try {
            RValue rval = (RValue) node;
            rval.rValue(dst, zone, e, log);
        } catch (ClassCastException fakse) {
            log.addException(new SemanticException("Expected R-value in brackets", token));
        }

        zone.removeAll(e.lv);
    }

    @Override
    public String toString() {
        return token.toString();
    }

    @Override
    public Type type(Environment e) {
        try {
            return ((RValue) node).type(e);
        } catch (ClassCastException fakse) {
            return new Type();
        }
    }

    @Override
    public boolean isRValue() {
        return node.isRValue();
    }

    @Override
    public boolean isLValue() {
        return node.isLValue();
    }

    @Override
    public void lValue(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(true, token);

        try {
            LValue lval = (LValue) node;
            lval.lValue(src, zone, e, log);
        } catch (ClassCastException fakse) {
            log.addException(new SemanticException("Expected L-value in brackets", token));
        }

        zone.removeAll(e.lv);
    }

}
