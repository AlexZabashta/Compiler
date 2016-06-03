package ast.node.misc;

import java.io.PrintWriter;

import lex.token.key_word.IfToken;
import misc.Type;
import ast.Node;
import ast.node.AbstractNode;
import ast.node.RValue;
import ast.node.op.RBracketsNode;
import code.Action;
import code.Environment;
import code.VisibilityZone;
import code.act.IfFalseJump;
import code.act.Jump;
import code.act.MoveVar;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.TypeMismatch;
import exception.UnexpectedVoidType;

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
        Jump jump = new Jump();

        Action tend = new code.act.Nop();
        Action fend = new code.act.Nop();

        jump.target = fend.label;

        try {
            Variable s = state.getVariable(z, e, log);
            IfFalseJump elseJump = new IfFalseJump(s);
            elseJump.target = tend.label;
            z.addAction(elseJump);
        } catch (ParseException parseException) {
            log.addException(parseException);
        }

        x.action(z, e, log);

        z.addAction(jump);
        z.addAction(tend);

        y.action(z, e, log);

        z.addAction(fend);
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
    public Type type(Environment e) throws DeclarationException {
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

    @Override
    public Variable getVariable(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            RValue xVal = (RValue) x;
            RValue yVal = (RValue) y;

            Variable res = z.createVariable(type(e));

            Jump jump = new Jump();

            Action tend = new code.act.Nop();
            Action fend = new code.act.Nop();

            jump.target = fend.label;

            try {
                Variable s = state.getVariable(z, e, log);
                IfFalseJump elseJump = new IfFalseJump(s);
                elseJump.target = tend.label;
                z.addAction(elseJump);
            } catch (ParseException exception) {
                log.addException(exception);
            }

            try {
                Variable tvar = xVal.getVariable(z, e, log);
                z.addAction(new MoveVar(res, tvar, null));
            } catch (ParseException exception) {
                log.addException(exception);
            }

            z.addAction(jump);
            z.addAction(tend);

            try {
                Variable fvar = yVal.getVariable(z, e, log);
                z.addAction(new MoveVar(res, fvar, null));
            } catch (ParseException exception) {
                log.addException(exception);
            }

            z.addAction(fend);
            return res;
        } catch (ClassCastException | UnexpectedVoidType | DeclarationException | TypeMismatch exception) {
            throw new SemanticException(exception, ifToken);
        }
    }

}
