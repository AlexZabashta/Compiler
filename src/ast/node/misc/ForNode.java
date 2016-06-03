package ast.node.misc;

import java.io.PrintWriter;

import lex.token.key_word.BoolToken;
import lex.token.key_word.ForToken;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.leaf.ConstValueNode;
import code.Action;
import code.Environment;
import code.VisibilityZone;
import code.act.IfTrueJump;
import code.act.Jump;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class ForNode extends AbstractNode {

    public final ForToken forToken;
    public final Node pre, post, action;
    public final RValue state;

    public ForNode(ForToken forToken, Node action) {
        this(forToken, new Nop(), new ConstValueNode(new BoolToken(false, forToken.location)), new Nop(), action);
    }

    public ForNode(ForToken forToken, Node pre, RValue state, Node post, Node action) {
        this.forToken = forToken;
        this.pre = pre;
        this.state = state;
        this.post = post;
        this.action = action;
    }

    public ForNode(ForToken forToken, RValue state, Node action) {
        this(forToken, new Nop(), state, new Nop(), action);
    }

    public ForNode(ForToken forToken, RValue state, Node post, Node action) {
        this(forToken, new Nop(), state, post, action);
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone fz = new VisibilityZone();

        Jump jump = new Jump();
        Action wnop = new code.act.Nop();
        Action snop = new code.act.Nop();
        jump.target = snop.label;

        pre.action(fz, e, log);

        fz.addAction(jump);
        fz.addAction(wnop);

        action.action(fz, e, log);

        post.action(fz, e, log);

        fz.addAction(snop);

        try {
            Variable s = state.getVariable(fz, e, log);
            IfTrueJump elseJump = new IfTrueJump(s);
            elseJump.target = wnop.label;
            fz.addAction(elseJump);
        } catch (ParseException exception) {
            log.addException(exception);
        }

        try {
            z.addZone(fz, e);
        } catch (DeclarationException exception) {
            log.addException(new SemanticException(exception, forToken));
        }

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.print("for (");
        pre.print(out);
        out.print(", ");
        state.print(out);
        out.print(", ");
        post.print(out);
        out.println(")");
        action.println(out, indent + 1);
    }

    @Override
    public void print(PrintWriter out) {
        out.print("for (");
        pre.print(out);
        out.print(", ");
        state.print(out);
        out.print(", ");
        post.print(out);
        out.print(") ");
        action.print(out);
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(forToken);
        pre.printTree(out, indent + 1);
        state.printTree(out, indent + 1);
        post.printTree(out, indent + 1);
        action.printTree(out, indent + 2);
    }

    @Override
    public String toString() {
        return forToken.toString();
    }

}
