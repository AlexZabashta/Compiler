package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import lex.Token;
import code.Action;

public class Nop extends Action {

    public Nop(String label) {
        super(label, null);
    }

    public Nop() {
        super(null);
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("nop");
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(label() + ":" + comment());
        programText.add("        nop");

    }

}
