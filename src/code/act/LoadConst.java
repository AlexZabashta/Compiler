package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import asm.com.Mov;
import asm.com.PushLabel;
import asm.mem.CpuRegister;
import asm.mem.Memory;
import asm.mem.RWMemory;
import asm.mem.RamLabel;
import ast.node.Values;
import code.Action;
import code.Variable;

public class LoadConst extends Action {
    public final Variable dst;
    public final String src;

    public LoadConst(Variable dst, int valIndex, String label, String comment) {
        super(label, comment);
        this.dst = dst;
        this.src = "val" + valIndex;

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(dst + " = load const(" + src + ")");
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());

        RWMemory memory = dst.memory();
        if (dst.type.level == 0) {
            if (memory.useRam()) {
                RWMemory eax = new CpuRegister();
                programText.add(new Mov(eax, new RamLabel(src), null, comment));
                programText.add(new Mov(memory, eax, null, comment));
            } else {
                programText.add(new Mov(memory, new RamLabel(src), null, comment));
            }
        } else {
            programText.add(new PushLabel(src, null, comment));
            programText.add(new asm.com.Call(Values.toString("sys.copy", dst.type), null, comment));
            programText.add(new Mov(memory, new CpuRegister(), null, comment));
        }
    }
}
