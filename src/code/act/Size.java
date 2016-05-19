package code.act;

import java.io.PrintWriter;
import java.util.List;

import misc.EnumType;
import misc.Type;
import asm.Command;
import asm.Register;
import asm.mem.ConstInt;
import asm.mem.CpuRegister;
import asm.mem.Memory;
import asm.mem.RWMemory;
import asm.mem.RamRegister;
import code.Action;
import code.var.LocalVariable;
import exception.TypeMismatch;

public class Size extends Action {
    public final LocalVariable res, val;

    public Size(LocalVariable res, LocalVariable val, String comment) throws TypeMismatch {
        super(null, comment);
        this.res = res;
        this.val = val;
        res.use(1);
        val.use(1);

        TypeMismatch.check(res.type, new Type(EnumType.INT));
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());

        Memory src = val.memory();
        RWMemory dst = res.rwMemory();
        Register eax = Register.EAX;

        if (val.type.dim == 0) {
            programText.add(new asm.com.Mov(dst, new ConstInt(32), null, null));
        } else {
            programText.add(new asm.com.Mov(new CpuRegister(eax), src, null, null));
            if (dst.useRam()) {
                programText.add(new asm.com.Mov(new CpuRegister(eax), new RamRegister(eax, 1), null, null));
                programText.add(new asm.com.Mov(dst, new CpuRegister(eax), null, null));
            } else {
                programText.add(new asm.com.Mov(dst, new RamRegister(eax, 1), null, null));
            }
        }
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(res + " = size " + val);
    }

}
