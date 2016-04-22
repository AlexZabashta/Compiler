package ast.node.misc;

import java.io.PrintWriter;
import java.util.List;

import lex.token.key_word.BoolToken;
import lex.token.key_word.ForToken;
import misc.EnumType;
import misc.Type;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.leaf.BoolNode;
import code.Action;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.IfFalseJump;
import code.act.IfTrueJump;
import code.act.Jump;

public class ForNode extends AbstractNode {

    public final ForToken forToken;
    public final Node pre, post, action;
    public final RValue state;

    public ForNode(ForToken forToken, Node action) {
        this(forToken, new Nop(), new BoolNode(new BoolToken(false, forToken.location)), new Nop(), action);
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
    public void action(VisibilityZone z, Environment e, List<String> errors) {
        VisibilityZone fz = z.subZone(true, forToken);

        Variable s = fz.createVariable(new Type(EnumType.BOOL));

        Jump jump = new Jump();
        Action wnop = new code.act.Nop();
        Action snop = new code.act.Nop();
        jump.target = snop.label();

        pre.action(fz, e, errors);

        fz.addAction(jump);
        fz.addAction(wnop);
        action.action(fz, e, errors);
        post.action(fz, e, errors);

        fz.addAction(snop);
        state.rValue(s, fz, e, errors);

        IfTrueJump elseJump = new IfTrueJump(s);
        elseJump.target = wnop.label();

        fz.addAction(elseJump);

        fz.removeAll(e.lv);
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
