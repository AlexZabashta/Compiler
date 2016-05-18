package ast.node.leaf;

import java.io.PrintWriter;

import lex.token.ConstValueToken;
import lex.token.key_word.BoolToken;
import misc.EnumType;
import misc.Type;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.Values;
import code.Environment;
import code.VisibilityZone;
import code.act.CopyConst;
import code.var.Variable;
import exception.Log;
import exception.ParseException;

public class ConstValueNode extends AbstractNode implements RValue {
    public final ConstValueToken token;

    public ConstValueNode(ConstValueToken token) {
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
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {

        z.addAction(new CopyConst(dst, token.variable, null, token.toString()));

    }

    @Override
    public Type type(Environment e) {
        return token.variable.type;
    }
}
