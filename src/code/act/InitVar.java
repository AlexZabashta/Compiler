package code.act;

import java.io.PrintWriter;
import java.util.List;

import misc.Type;
import asm.Command;
import asm.mem.Memory;
import asm.mem.RWMemory;
import code.Action;
import code.var.LocalVariable;
import code.var.Variable;
import code.var.Variable;
import exception.TypeMismatch;

public class InitVar extends Action {
    public final LocalVariable var;

    public InitVar(LocalVariable var, String comment) throws TypeMismatch {
        super(null, comment);
        this.var = var;
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());
        Variable.init(programText, var.type, var.rwMemory());
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("init(" + var + ")");
    }

}
