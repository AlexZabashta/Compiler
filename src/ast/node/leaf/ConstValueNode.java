package ast.node.leaf;

import java.io.PrintWriter;

import lex.token.ConstValueToken;
import misc.Type;
import ast.node.AbstractNode;
import ast.node.RValue;
import code.Environment;
import code.VisibilityZone;
import code.act.CopyConst;
import code.var.Variable;
import exception.Log;
import exception.ParseException;
import exception.UnexpectedVoidType;

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
    public Variable getVariable(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Variable variable = z.createVariable(token.type());
            z.addAction(new CopyConst(variable, token.variable, null, token.toString()));
            return variable;
        } catch (UnexpectedVoidType neverHappen) {
            throw new RuntimeException(neverHappen);
        }
    }

    @Override
    public Type type(Environment e) {
        return token.variable.type;
    }
}
