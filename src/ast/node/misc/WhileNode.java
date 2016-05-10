package ast.node.misc;

import java.io.PrintWriter;

import lex.token.key_word.WhileToken;
import misc.EnumType;
import misc.Type;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.op.RBracketsNode;
import code.Action;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.IfFalseJump;
import code.act.IfTrueJump;
import code.act.Jump;
import exception.Log;
import exception.ParseException;

public class WhileNode extends AbstractNode {

    public final Node action;
    public final RBracketsNode state;
    public final WhileToken whileToken;

    public WhileNode(WhileToken whileToken, RBracketsNode state, Node action) {
        this.whileToken = whileToken;
        this.state = state;
        this.action = action;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone wz = z.subZone(true, whileToken);

        Variable s = wz.createVariable(new Type(EnumType.BOOL));

        Jump jump = new Jump();
        Action wnop = new code.act.Nop();
        Action snop = new code.act.Nop();
        jump.target = snop.label();

        wz.addAction(jump);
        wz.addAction(wnop);
        action.action(wz, e, log);
        wz.addAction(snop);

        state.rValue(s, wz, e, log);

        IfTrueJump elseJump = new IfTrueJump(s);
        elseJump.target = wnop.label();

        wz.addAction(elseJump);
        wz.removeAll(e.lv);
    }

    @Override
    public void print(PrintWriter out) {
        out.print("while ");
        state.print(out);
        out.print(' ');
        action.print(out);
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.print("while ");
        state.print(out);
        out.println();
        action.println(out, indent + 1);
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(whileToken);
        state.printTree(out, indent + 1);
        action.printTree(out, indent + 2);
    }

    @Override
    public String toString() {
        return whileToken.toString();
    }

    @Override
    public boolean isRValue() {
        return false;
    }

    @Override
    public boolean isLValue() {
        return false;
    }
}
