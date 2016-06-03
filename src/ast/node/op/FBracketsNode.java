package ast.node.op;

import java.io.PrintWriter;

import lex.Token;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.misc.Nop;
import code.Environment;
import code.VisibilityZone;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class FBracketsNode extends AbstractNode {

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
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone zone = new VisibilityZone();
        node.action(zone, e, log);

        try {
            z.addZone(zone, e);
        } catch (DeclarationException exception) {
            log.addException(new SemanticException(exception, token));
        }
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
    public String toString() {
        return token.toString();
    }

}
