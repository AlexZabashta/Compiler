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

        if (res == null || res.type.idVoid()) {
            res = null;
        }

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

    @Override
    public void asm(List<String> programText, List<String> errors) {
        int sp = 0;
        programText.add(label() + ":" + comment());

        if (res != null) {
            if (res.type.level == 0) {
                programText.add("        push dword 0");
            } else {
                programText.add("        push dword emptyarray");
            }
            ++sp;
        }

        for (Variable var : args) {
            programText.add("        push dword [esp + " + ((var.distance(parent) + sp) * 4) + "]");
            ++sp;
        }

        programText.add("        call " + fun);

        if (res != null) {
            programText.add("        sub esp, " + ((sp - 1) * 4));
            programText.add("        pop eax");
            programText.add("        mov [esp + " + (res.distance(parent) * 4) + "], eax");
        } else {
            programText.add("        sub esp, " + ((sp - 0) * 4));
        }

        // programText.add(" mov eax, [esp + " + (state.distance(parent) * 4) +
        // "]");
        // programText.add(" cmp eax, eax");

    }

}
