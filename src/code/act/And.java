package code.act;

import java.io.PrintWriter;
import java.util.List;

import misc.EnumType;
import misc.Type;
import asm.Command;
import asm.mem.CpuRegister;
import asm.mem.Memory;
import asm.mem.RWMemory;
import code.Action;
import code.var.Variable;
import exception.TypeMismatch;

public class And extends Action {
    public final Variable res, x, y;

    public And(Variable res, Variable x, Variable y, String comment) throws TypeMismatch {
        super(null, comment);

        this.res = res;
        this.x = x;
        this.y = y;
        res.use(2);
        x.use(1);
        y.use(1);
        if (res.type.equals(new Type(EnumType.INT))) {
            TypeMismatch.check(res.type, x.type);
            TypeMismatch.check(res.type, y.type);
        } else {
            TypeMismatch.check(res.type, new Type(EnumType.BOOL));
            TypeMismatch.check(res.type, x.type);
            TypeMismatch.check(res.type, y.type);
        }
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());

        Memory xMem = x.memory(), yMem = y.memory();
        RWMemory resMem = res.rwMemory();

        if (resMem.useRam() && (xMem.useRam() | yMem.useRam())) {
            CpuRegister eax = new CpuRegister();

            programText.add(new asm.com.Mov(eax, xMem, null, null));
            programText.add(new asm.com.And(eax, yMem, null, null));
            programText.add(new asm.com.Mov(resMem, eax, null, null));
        } else {
            programText.add(new asm.com.Mov(resMem, xMem, null, null));
            programText.add(new asm.com.And(resMem, yMem, null, null));
        }
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(res + " = " + x + " and " + y);
    }

}
