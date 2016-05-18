package code.act;

import java.io.PrintWriter;
import java.util.List;

import misc.Type;
import asm.Command;
import asm.mem.Memory;
import asm.mem.RWMemory;
import code.Action;
import code.var.Variable;
import exception.TypeMismatch;

public class MoveVar extends Action {
    public final Variable src, dst;
    public final Type type;

    public MoveVar(Variable dst, Variable src, String comment) throws TypeMismatch {
        super(null, comment);
        this.dst = dst;
        this.src = src;
        this.type = dst.type;
        if (!dst.type.equals(src.type)) {
            throw new TypeMismatch(dst.type, src.type);
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
            Variable.subscribe(programText, srcMemory);
            Variable.unsubscribe(programText, type, dstMemory);
            Variable.moveMem(programText, dstMemory, srcMemory);
        }
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(dst + " = " + src);
    }

}
