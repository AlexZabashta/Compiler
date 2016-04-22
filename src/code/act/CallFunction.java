package code.act;

import java.io.PrintWriter;
import java.util.List;

import lex.Token;
import code.Action;
import code.Variable;

public class CallFunction extends Action {

    public final String fun;
    public final List<Variable> args;
    public final Variable res;

    public CallFunction(Variable res, String fun, List<Variable> args, String label, Token token) {
        super(label, token);
        this.fun = fun;
        this.args = args;
        this.res = res;
    }

    @Override
    public String toString() {
        return fun + "()";
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);

        if (res == null) {
            out.println(fun + "(" + args + ")");
        } else {
            out.println(res + " = " + fun + "(" + args + ")");
        }
    }

}
