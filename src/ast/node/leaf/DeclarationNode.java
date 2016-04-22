package ast.node.leaf;

import java.io.PrintWriter;
import java.util.List;

import lex.token.fold.DeclarationToken;
import ast.node.AbstractNode;
import ast.node.LValue;
import ast.node.Values;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.SetLVar;

public class DeclarationNode extends AbstractNode implements LValue {
    public final DeclarationToken token;

    public DeclarationNode(DeclarationToken token) {
        this.token = token;
    }

    @Override
    public void action(VisibilityZone z, Environment e, List<String> errors) {
        z.createVariable(token, e.lv, errors);
    }

    @Override
    public void lValue(Variable src, VisibilityZone z, Environment e, List<String> errors) {
        Variable dst = z.createVariable(token, e.lv, errors);
        if (Values.cmp(dst, src, errors, token)) {
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

}
