package ast.node.misc;

import java.io.PrintWriter;

import lex.token.key_word.ReturnToken;
import ast.node.AbstractNode;
import code.Environment;
import code.VisibilityZone;
import code.act.Break;
import code.var.Variable;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

@Deprecated
public class ReturnNode extends AbstractNode {

    public final ReturnToken returnToken;

    public ReturnNode(ReturnToken returnToken) {
        throw new RuntimeException("Deprecated");
        // this.returnToken = returnToken;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        // Variable res = z.root().result;
        //
        // if (res == null) {
        // z.addAction(new Break(z.level + 1, null, returnToken.toString()));
        // } else {
        // log.addException(new SemanticException("Missing return value", returnToken));
        // }
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
