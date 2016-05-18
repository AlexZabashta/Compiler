package code.act;

import java.io.PrintWriter;
import java.util.List;

import misc.Type;
import asm.Command;
import asm.com.Call;
import asm.com.PopNull;
import asm.com.Push;
import asm.mem.CpuRegister;
import asm.mem.Memory;
import asm.mem.RWMemory;
import ast.node.Values;
import code.Action;
import code.var.ConstVariable;
import code.var.Variable;

public class CopyConst extends Action {
    public final Variable dst;
    public final ConstVariable src;
    public final Type type;

    public CopyConst(Variable dst, ConstVariable src, String label, String comment) {
        super(label, comment);
        this.dst = dst;
        this.src = src;
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
            Variable.moveMem(programText, dstMemory, srcMemory);
        } else {
            Variable.unsubscribe(programText, type, dstMemory);

            programText.add(new Push(srcMemory, null, null));
            programText.add(new Call(Values.toString("sys.clone", type), null, null));
            programText.add(new PopNull(1, null, null));

            Variable.moveMem(programText, dstMemory, new CpuRegister());
        }
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(dst + " = load const(" + src + ")");
    }
}
