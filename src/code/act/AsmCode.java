package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import code.Action;

public class AsmCode extends Action {

    public final Command command;

    public AsmCode(Command command) {
        super(null, null);
        this.command = command;
    }

    @Override
    public String toString() {
        return "asm " + command;
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("asm " + command);
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(command);

    }

}
