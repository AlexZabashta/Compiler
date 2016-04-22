package code.act;

import java.io.PrintWriter;

import lex.Token;
import code.Action;
import code.Variable;

public class IfTrueJump extends Action {

    public String target;
    public final Variable state;

    public IfTrueJump(Variable state) {
        super(null);
        this.state = state;
    }

    @Override
    public String toString() {
        return "else (" + state + ") jump to " + target;
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("if (" + state + ") jump to " + target);
    }

}
