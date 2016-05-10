package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import lex.Token;
import code.Action;
import code.Variable;

public class Jump extends Action {

    public String target;

    public Jump() {
        super(null);
    }

    @Override
    public String toString() {
        return "jump to " + target;
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("jump to " + target);
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(label() + ":" + comment());
        programText.add("        jmp " + target);
    }

}
