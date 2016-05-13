package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import asm.com.Mov;
import asm.com.PushLabel;
import asm.mem.CpuRegister;
import asm.mem.RWMemory;
import asm.mem.RamLabel;
import ast.node.Values;
import code.Action;
import code.Variable;

public class LoadGVar extends Action {
    public final String src;
    public final Variable dst;

    public LoadGVar(Variable dst, String src, String comment) {
        super(null, comment);
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
        programText.add(start());

        RWMemory memory = dst.memory();
        if (memory.useRam()) {
            RWMemory eax = new CpuRegister();
            programText.add(new Mov(eax, new RamLabel(src), null, comment));
            programText.add(new Mov(memory, eax, null, comment));
        } else {
            programText.add(new Mov(memory, new RamLabel(src), null, comment));
        }
    }

}
