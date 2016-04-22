package ast.node.misc;

import java.io.PrintWriter;
import java.util.List;

import lex.token.key_word.ReturnToken;
import ast.node.AbstractNode;
import ast.node.RValue;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.Break;

public class ReturnVNode extends AbstractNode {

    public final RValue node;
    public final ReturnToken returnToken;

    public ReturnVNode(RValue node, ReturnToken returnToken) {
        this.node = node;
        this.returnToken = returnToken;
    }

    @Override
    public void action(VisibilityZone z, Environment e, List<String> errors) {
        VisibilityZone rz = z.subZone(false, returnToken);
        Variable res = z.root().result;

        if (res == null || res.type.idVoid()) {
            errors.add("Can't return value in void function at " + returnToken);
        } else {
            node.rValue(res, rz, e, errors);
            z.addAction(new Break(z.level + 1, null, returnToken));
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
