package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import asm.com.Jmp;
import code.Action;
import code.VisibilityZone;

public class Break extends Action {

    public final int level;

    public Break(int level, String label, String comment) {
        super(label, comment);
        this.level = level;
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());
        VisibilityZone cur = parent;

        Nop nop = null;

        for (int n = level; n > 0; n--) {
            cur.freeVars(programText);
            nop = cur.end();
            cur = cur.parent();
        }

        programText.add(new Jmp(nop.label, null, comment));
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("break");
    }

    @Override
    public String toString() {
        return "break";
    }

}
