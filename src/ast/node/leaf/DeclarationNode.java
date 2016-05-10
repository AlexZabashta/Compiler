package ast.node.leaf;

import java.io.PrintWriter;

import lex.token.fold.DeclarationToken;
import ast.node.AbstractNode;
import ast.node.LValue;
import ast.node.Values;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.SetLVar;
import exception.Log;
import exception.ParseException;

public class DeclarationNode extends AbstractNode implements LValue {
    public final DeclarationToken token;

    public DeclarationNode(DeclarationToken token) {
        this.token = token;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        z.createVariable(token, e.lv, log);
    }

    @Override
    public void lValue(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException {
        Variable dst = z.createVariable(token, e.lv, log);
        if (Values.cmp(dst, src, log, token)) {
            z.addAction(new SetLVar(dst, src, token));
        }
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
    public boolean isRValue() {
        return false;
    }

    @Override
    public boolean isLValue() {
        return true;
    }

}
