package ast.node.misc;

import java.io.PrintWriter;
import java.util.List;

import lex.token.key_word.ReturnToken;
import ast.node.AbstractNode;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.Break;

public class ReturnNode extends AbstractNode {

    public final ReturnToken returnToken;

    public ReturnNode(ReturnToken returnToken) {
        this.returnToken = returnToken;
    }

    @Override
    public void action(VisibilityZone z, Environment e, List<String> errors) {
        Variable res = z.root().result;

        if (res == null || res.type.idVoid()) {
            z.addAction(new Break(z.level + 1, null, returnToken));
        } else {
            errors.add("Missing return value after " + returnToken);
        }
    }

    @Override
    public void print(PrintWriter out) {
        out.print(returnToken.toTokenString());
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(returnToken);
    }

    @Override
    public String toString() {
        return returnToken.toString();
    }

}
