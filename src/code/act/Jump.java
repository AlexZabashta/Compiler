package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import asm.mem.ConstInt;
import code.Action;

public class Jump extends Action {

    public String target;

    public Jump() {
        super(null, null);
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
        programText.add(start());
        programText.add(new asm.com.Jmp(target, null, comment));
    }

}
