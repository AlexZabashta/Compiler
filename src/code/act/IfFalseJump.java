package code.act;

import java.io.PrintWriter;
import java.util.List;

import lex.Token;
import code.Action;
import code.Variable;

public class IfFalseJump extends Action {

    public String target;
    public final Variable state;

    public IfFalseJump(Variable state) {
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
        out.println("if (" + state + " == false) jump to " + target);
    }

    @Override
    public void asm(List<String> programText, List<String> errors) {
        programText.add(label() + ":" + comment());
        programText.add("        mov eax, [esp + " + (state.distance(parent) * 4) + "]");
        programText.add("        cmp eax, eax");
        programText.add("        je " + target);
    }
}
