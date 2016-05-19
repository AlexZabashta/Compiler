package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import asm.mem.ConstInt;
import code.Action;
import code.var.LocalVariable;

public class IfFalseJump extends Action {

    public final LocalVariable state;
    public String target;

    public IfFalseJump(LocalVariable state) {
        super(null, null);
        this.state = state;
        state.use(1);

    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());
        programText.add(new asm.com.Cmp(state.rwMemory(), new ConstInt(0), null, null));
        programText.add(new asm.com.Je(target, null, null));
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("if (" + state + " == false) jump to " + target);
    }

    @Override
    public String toString() {
        return "if (" + state + " == false) jump to " + target;
    }
}
