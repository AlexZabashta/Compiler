package code.act;

import java.io.PrintWriter;

import lex.Token;
import code.Action;
import code.Variable;

public class LoadConst extends Action {
    public final Variable variable;
    public final int valIndex;

    public LoadConst(Variable variable, int valIndex, String label, Token token) {
        super(label, token);
        this.variable = variable;
        this.valIndex = valIndex;

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(variable + " = load const(" + valIndex + ")");
    }

}
