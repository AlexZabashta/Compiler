package code.var;

import java.util.List;

import exception.UnexpectedVoidType;
import misc.Type;
import asm.Command;
import asm.com.Call;
import asm.com.Mov;
import asm.com.ShiftEsp;
import asm.com.Push;
import asm.mem.ConstInt;
import asm.mem.CpuRegister;
import asm.mem.Label;
import asm.mem.Memory;
import asm.mem.RWMemory;
import ast.SystemFunction;
import ast.node.Values;

public abstract class Variable {
    public static void moveMem(List<Command> programText, RWMemory dst, Memory src) {
        if (src.useRam() && dst.useRam()) {
            RWMemory eax = new CpuRegister();
            programText.add(new Mov(eax, src, null, null));
            programText.add(new Mov(dst, eax, null, null));
        } else {
            programText.add(new Mov(dst, src, null, null));
        }
    }

    public void use(int cnt) {
    }

    public static void subscribe(List<Command> programText, Type type, Memory src) {
        if (type.dim == 0) {
            return;
        }
        programText.add(new Push(src, null, null));
        programText.add(new Call(SystemFunction.PAC + ".subscribe", null, null));
        programText.add(new ShiftEsp(1, null, null));
    }

    public static void init(List<Command> programText, Type type, RWMemory dst) {
        if (type.dim == 0) {
            moveMem(programText, dst, ConstVariable.FALSE);
        } else {
            moveMem(programText, dst, ConstVariable.NULL);
        }
    }

    public static void unsubscribe(List<Command> programText, Type type, Memory src) {
        if (type.dim == 0) {
            return;
        }
        programText.add(new Push(src, null, null));
        programText.add(new Call(Values.toString(SystemFunction.PAC + ".unsubscribe", type), null, null));
        programText.add(new ShiftEsp(1, null, null));
    }

    public final Type type;

    public Variable(Type type) throws UnexpectedVoidType {
        if (type.idVoid()) {
            throw new UnexpectedVoidType("Can't create void variable");
        }
        this.type = type;
    }

    public Memory memory() {
        return rwMemory();
    }

    public abstract RWMemory rwMemory();
}
