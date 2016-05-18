package ast.node.misc;

import java.io.PrintWriter;

import lex.token.key_word.ReturnToken;
import ast.node.AbstractNode;
import ast.node.RValue;
import code.Environment;
import code.VisibilityZone;
import code.act.Break;
import code.var.Variable;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class ReturnVNode extends AbstractNode {

    public final RValue node;
    public final ReturnToken returnToken;

    public ReturnVNode(RValue node, ReturnToken returnToken) {
        this.node = node;
        this.returnToken = returnToken;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone rz = z.subZone(false, returnToken.toString());
        Variable res = z.root().result;

        if (res == null) {
            log.addException(new SemanticException("Can't return value in void function", returnToken));
        } else {
            node.getVariable(res, rz, e, log);
            z.addAction(new Break(z.level + 1, null, returnToken.toString()));
        }

    }

    @Override
    public void print(PrintWriter out) {
        out.print(returnToken.toTokenString());
        out.print(' ');
        node.print(out);
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(returnToken);
        node.printTree(out, indent + 1);
    }

    @Override
    public String toString() {
        return returnToken.toString();
    }

}
