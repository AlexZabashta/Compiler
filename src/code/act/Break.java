package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import lex.Token;
import code.Action;
import code.Variable;
import code.VisibilityZone;

public class Break extends Action {

    public final int level;

    public Break(int level, String label, Token token) {
        super(label, token);
        this.level = level;
    }

    @Override
    public String toString() {
        return "break";
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("break");
    }

    @Override
    public void asm(List<Command> programText) {
        VisibilityZone cur = parent;
        int sp = 0;

        Nop nop = null;

        for (int n = level; n > 0; n--) {
            sp += cur.numberOfVars();
            nop = cur.end();
            cur = cur.parent();
        }

        programText.add(label() + ":" + comment());
        programText.add("        add esp, " + (sp * 4));
        programText.add("        jmp " + nop.label());

    }

}
