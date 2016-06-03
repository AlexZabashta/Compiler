package code.act;

import java.io.PrintWriter;
import java.util.List;

import misc.EnumType;
import misc.Type;
import asm.Command;
import asm.mem.ConstInt;
import asm.mem.CpuRegister;
import asm.mem.Memory;
import asm.mem.RWMemory;
import code.Action;
import code.var.Variable;
import exception.TypeMismatch;

public class Not extends Action {
    public final Variable res, val;

    public Not(Variable res, Variable val, String comment) throws TypeMismatch {
        super(null, comment);

        this.res = res;
        this.val = val;
        res.use(1);
        val.use(1);
        if (res.type.equals(new Type(EnumType.INT))) {
            TypeMismatch.check(res.type, val.type);
        } else {
            TypeMismatch.check(res.type, new Type(EnumType.BOOL));
            TypeMismatch.check(res.type, val.type);
        }
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());

        Memory src = val.memory();
        RWMemory dst = res.rwMemory();
        CpuRegister eax = new CpuRegister();

        programText.add(new asm.com.Mov(eax, new ConstInt(-1), null, null));
        programText.add(new asm.com.Xor(eax, src, null, null));
        programText.add(new asm.com.Mov(dst, eax, null, null));

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(res + " = not " + val);
    }

}
