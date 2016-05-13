package ast.node.misc;

import java.io.PrintWriter;

import lex.token.key_word.IfToken;
import misc.EnumType;
import misc.Type;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.op.RBracketsNode;
import code.Action;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.IfFalseJump;
import code.act.Jump;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class IfNode extends AbstractNode implements RValue {

    public final IfToken ifToken;
    public final RBracketsNode state;
    public final Node x, y;

    public IfNode(IfToken ifToken, RBracketsNode state, Node x, Node y) {
        this.ifToken = ifToken;
        this.state = state;
        this.x = x;
        this.y = y;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        VisibilityZone iz = z.subZone(false, ifToken.toString());
        Variable s = iz.createVariable(new Type(EnumType.BOOL));

        state.getVariable(s, iz, e, log);
        IfFalseJump elseJump = new IfFalseJump(s);
        Jump jump = new Jump();

        Action tend = new code.act.Nop();
        Action fend = new code.act.Nop();

        elseJump.target = tend.label;
        jump.target = fend.label;

        iz.addAction(elseJump);
        x.action(iz, e, log);

        iz.addAction(jump);
        iz.addAction(tend);
        y.action(iz, e, log);
        iz.addAction(fend);
    }

    @Override
    public void print(PrintWriter out) {
        out.print("if ");
        state.print(out);
        out.print(' ');
        x.print(out);
        out.print(" else ");
        y.print(out);
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.print("if ");
        state.print(out);
        out.println();
        x.println(out, indent + 1);
        out.println();
        printIndent(out, indent);
        out.println("else");
        y.println(out, indent + 1);
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println("if");
        state.printTree(out, indent);
        x.printTree(out, indent + 1);
        printIndent(out, indent);
        out.println("else");
        y.printTree(out, indent + 1);
    }

    @Override
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            RValue xVal = (RValue) x;
            RValue yVal = (RValue) y;

            VisibilityZone iz = z.subZone(false, ifToken.toString());
            Variable s = iz.createVariable(new Type(EnumType.BOOL));

            state.getVariable(s, iz, e, log);
            IfFalseJump elseJump = new IfFalseJump(s);
            Jump jump = new Jump();

            Action tend = new code.act.Nop();
            Action fend = new code.act.Nop();

            elseJump.target = tend.label;
            jump.target = fend.label;

            iz.addAction(elseJump);
            xVal.getVariable(dst, iz, e, log);

            iz.addAction(jump);
            iz.addAction(tend);

            yVal.getVariable(dst, iz, e, log);

            iz.addAction(fend);
        } catch (ClassCastException fake) {
            log.addException(new SemanticException("Unexpected void node", ifToken));
        }
    }

    @Override
    public Type type(Environment e) {
        try {
            Type xType = ((RValue) x).type(e);
            Type yType = ((RValue) y).type(e);

            if (xType.equals(yType)) {
                return xType;
            }
        } catch (ClassCastException fake) {
        }
        return new Type();
    }

}
