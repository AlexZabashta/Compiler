package code.act;

import java.io.PrintWriter;

import lex.Token;
import code.Action;
import code.Variable;

public class SetGVar extends Action {
    public final Variable src;
    public final String dst;

    public SetGVar(String dst, Variable src, String label, Token token) {
        super(label, token);
        this.dst = dst;
        this.src = src;

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(dst + " = " + src);
    }

}
