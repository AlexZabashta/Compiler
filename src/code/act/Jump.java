package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import code.Action;

public class Jump extends Action {

    public String target;

    public Jump() {
        super(null, null);
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(new asm.com.Jmp(target, label, comment));
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("jump to " + target);
    }

    @Override
    public String toString() {
        return "jump to " + target;
    }

}
