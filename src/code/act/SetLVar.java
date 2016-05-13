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

public class SetLVar extends Action {
    public final Variable src, dst;

    public SetLVar(Variable dst, Variable src, String comment) {
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

        RWMemory srcMem = src.memory();
        RWMemory dstMem = dst.memory();

        if (srcMem.useRam() && dstMem.useRam()) {
            RWMemory eax = new CpuRegister();
            programText.add(new Mov(eax, srcMem, null, comment));
            programText.add(new Mov(dstMem, eax, null, comment));
        } else {
            programText.add(new Mov(dstMem, srcMem, null, comment));
        }
    }

}
