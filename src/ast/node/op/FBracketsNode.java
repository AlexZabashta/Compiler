package ast.node.op;

import java.io.PrintWriter;
import java.util.List;

import lex.Token;
import misc.Type;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.misc.Nop;
import code.Environment;
import code.Variable;
import code.VisibilityZone;

public class FBracketsNode extends AbstractNode implements RValue {

    public final Node node;
    public final Token token;

    public FBracketsNode(Node node, Token token) {
        this.node = node;
        this.token = token;
    }

    public FBracketsNode(Token token) {
        this.node = new Nop();
        this.token = token;
    }

    @Override
    public void action(VisibilityZone z, Environment e, List<String> errors) {
        rValue(null, z, e, errors);
    }

    @Override
    public void print(PrintWriter out) {
        out.print('{');
        node.print(out);
        out.print('}');
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println('{');
        node.println(out, indent + 1);
        out.println();
        printIndent(out, indent);
        out.print('}');
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(token);
        node.printTree(out, indent + 1);
    }

    @Override
    public void rValue(Variable dst, VisibilityZone z, Environment e, List<String> errors) {
        VisibilityZone zone = z.subZone(true, token);
        if (dst == null || dst.type.idVoid()) {
            node.action(zone, e, errors);
        } else {
            try {
                RValue rval = (RValue) node;
                rval.rValue(dst, zone, e, errors);
            } catch (ClassCastException fakse) {
                errors.add("Unexpected void in " + token);
            }
        }
        zone.removeAll(e.lv);
    }

    @Override
    public String toString() {
        return token.toString();
    }

    @Override
    public Type type(Environment e) {
        try {
            return ((RValue) node).type(e);
        } catch (ClassCastException fakse) {
            return new Type();
        }
    }

}
