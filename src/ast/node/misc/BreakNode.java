package ast.node.misc;

import java.io.PrintWriter;

import lex.token.fold.BreakToken;
import ast.node.AbstractNode;
import code.Environment;
import code.VisibilityZone;
import code.act.Break;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

@Deprecated
public class BreakNode extends AbstractNode {

    public final BreakToken breakToken;

    public BreakNode(BreakToken breakToken) {
        throw new RuntimeException("Deprecated");
        // this.breakToken = breakToken;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        // int n = breakToken.level, m = 0;
        //
        // VisibilityZone cur = z;
        //
        // while (n > 0 && cur != null) {
        // if (cur.visible) {
        // --n;
        // }
        // ++m;
        // cur = cur.parent();
        // }
        //
        // if (n == 0) {
        // z.addAction(new Break(m, null, breakToken.toString()));
        // } else {
        // log.addException(new SemanticException(n + " visibility zones remain", breakToken));
        // }
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
