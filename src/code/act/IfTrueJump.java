package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import asm.mem.ConstInt;
import code.Action;
import code.var.LocalVariable;

public class IfTrueJump extends Action {

    public final LocalVariable state;
    public String target;

    public IfTrueJump(LocalVariable state) {
        super(null, null);
        this.state = state;
        state.use(1);
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());
        programText.add(new asm.com.Cmp(state.rwMemory(), new ConstInt(0), null, null));
        programText.add(new asm.com.Jne(target, null, null));
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("if (" + state + ") jump to " + target);
    }

    @Override
    public String toString() {
        return "else (" + state + ") jump to " + target;
    }

}
