package ast.node.op;

import java.io.PrintWriter;

import lex.Token;
import misc.Type;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.LValue;
import ast.node.RValue;
import code.Environment;
import code.VisibilityZone;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class RBracketsNode extends AbstractNode implements LValue, RValue {

    public final Node node;
    public final Token token;

    public RBracketsNode(Node node, Token token) {
        this.node = node;
        this.token = token;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(false, token.toString());
        node.action(zone, e, log);
    }

    @Override
    public void print(PrintWriter out) {
        out.print('(');
        node.print(out);
        out.print(')');

    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(token);
        node.printTree(out, indent + 1);
    }

    @Override
    public String toString() {
        return token.toString();
    }

    @Override
    public Type type(Environment e) throws DeclarationException {
        try {
            return ((RValue) node).type(e);
        } catch (ClassCastException fakse) {
            return new Type();
        }
    }

    @Override
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(false, token.toString());

        try {
            RValue rval = (RValue) node;
            rval.getVariable(dst, zone, e, log);
        } catch (ClassCastException fakse) {
            log.addException(new SemanticException("Expected R-value in brackets", token));
        }
    }

    @Override
    public void setVariable(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = z.subZone(false, token.toString());
        try {
            LValue lval = (LValue) node;
            lval.setVariable(src, zone, e, log);
        } catch (ClassCastException fakse) {
            log.addException(new SemanticException("Expected L-value in brackets", token));
        }
    }

}
