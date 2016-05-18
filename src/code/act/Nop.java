package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import code.Action;

public class Nop extends Action {

    public Nop() {
        super(null, null);
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(new asm.com.Nop(label, comment));

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("nop");
    }

}
