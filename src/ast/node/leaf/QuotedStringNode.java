package ast.node.leaf;

import java.io.PrintWriter;

import lex.token.pure.QuotedString;
import misc.EnumType;
import misc.Type;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.Values;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.LoadConst;
import exception.Log;
import exception.ParseException;

public class QuotedStringNode extends AbstractNode implements RValue {
    public final QuotedString token;

    public QuotedStringNode(QuotedString token) {
        this.token = token;
    }

    @Override
    public void print(PrintWriter out) {
        out.print(token.toTokenString());
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(token);
    }

    @Override
    public void rValue(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        if (Values.cmp(dst.type, type(e), log, token)) {
            z.addAction(new LoadConst(dst, token.getValIndex(), null, token));
        }
    }

    @Override
    public Type type(Environment e) {
        return new Type(EnumType.CHAR, 1);
    }

    @Override
    public boolean isRValue() {
        return true;
    }

    @Override
    public boolean isLValue() {
        return false;
    }

}
