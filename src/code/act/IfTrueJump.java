package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import asm.mem.ConstInt;
import code.Action;
import code.Variable;

public class IfTrueJump extends Action {

    public String target;
    public final Variable state;

    public IfTrueJump(Variable state) {
        super(null, null);
        this.state = state;
    }

    @Override
    public String toString() {
        return "else (" + state + ") jump to " + target;
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("if (" + state + ") jump to " + target);
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());
        programText.add(new asm.com.Cmp(state.memory(), new ConstInt(0), null, comment));
        programText.add(new asm.com.Jne(target, null, comment));
    }

}
