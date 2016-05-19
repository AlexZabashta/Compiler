package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import asm.com.Call;
import asm.com.ShiftEsp;
import asm.com.Push;
import asm.mem.CpuRegister;
import asm.mem.Memory;
import asm.mem.RWMemory;
import ast.node.Values;
import code.Action;
import code.var.ConstVariable;
import code.var.LocalVariable;
import misc.Type;

public class CopyConst extends Action {
    public final LocalVariable dst;
    public final ConstVariable src;
    public final Type type;

    public CopyConst(LocalVariable dst, ConstVariable src, String label, String comment) {
        super(label, comment);
        this.dst = dst;
        this.src = src;

        dst.use(2);
        src.use(1);

        this.type = dst.type;
        if (!dst.type.equals(src.type)) {
            throw new RuntimeException("Wrong types " + dst + " " + src);
        }
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());

        RWMemory dstMemory = dst.rwMemory();
        Memory srcMemory = src.memory();

        if (dstMemory.equals(srcMemory)) {
            return;
        }
        if (type.dim == 0) {
            LocalVariable.moveMem(programText, dstMemory, srcMemory);
        } else {
            LocalVariable.unsubscribe(programText, type, dstMemory);

            programText.add(new Push(srcMemory, null, null));
            programText.add(new Call(Values.toString("sys.clone", type), null, null));
            programText.add(new ShiftEsp(1, null, null));

            LocalVariable.moveMem(programText, dstMemory, new CpuRegister());
        }
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(dst + " = load const(" + src + ")");
    }
}
