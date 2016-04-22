package code.act;

import java.io.PrintWriter;

import lex.Token;
import code.Action;
import code.Variable;

public class Break extends Action {

    public final int level;

    public Break(int level, String label, Token token) {
        super(label, token);
        this.level = level;
    }

    @Override
    public String toString() {
        return "break";
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("break");
    }

}
