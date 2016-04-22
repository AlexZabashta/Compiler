package code.act;

import java.io.PrintWriter;

import lex.Token;
import code.Action;
import code.Variable;

public class SetLVar extends Action {
    public final Variable src, dst;

    public SetLVar(Variable dst, Variable src, Token token) {
        super(token);
        this.dst = dst;
        this.src = src;

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(dst + " = " + src);
    }

}
