package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import code.Action;
import code.Variable;

public class SetGVar extends Action {
    public final Variable src;
    public final String dst;

    public SetGVar(String dst, Variable src, String label, String comment) {
        super(label, comment);
        this.dst = dst;
        this.src = src;

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(dst + " = " + src);
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(label() + ":" + comment());
        programText.add("        mov eax, [esp + " + (src.distance(parent) * 4) + "]");
        programText.add("        mov [" + dst + "], eax");
    }

}
