package ast.node.misc;

import java.io.PrintWriter;
import java.util.List;

import lex.token.fold.BreakToken;
import ast.node.AbstractNode;
import code.Environment;
import code.VisibilityZone;
import code.act.Break;

public class BreakNode extends AbstractNode {

    public final BreakToken breakToken;

    public BreakNode(BreakToken breakToken) {
        this.breakToken = breakToken;
    }

    @Override
    public void action(VisibilityZone z, Environment e, List<String> errors) {
        int n = breakToken.level, m = 0;

        VisibilityZone cur = z;

        while (n > 0 && cur != null) {
            if (cur.visible) {
                --n;
            }
            ++m;
            cur = cur.parent;
        }

        if (n == 0) {
            z.addAction(new Break(m, null, breakToken));
        } else {
            errors.add(n + " visibility zones remain at " + breakToken);
        }
    }

    @Override
    public void print(PrintWriter out) {
        out.print(breakToken.toTokenString());
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(breakToken);
    }

    @Override
    public String toString() {
        return breakToken.toString();
    }

}
