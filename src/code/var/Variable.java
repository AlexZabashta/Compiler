package code.var;

import java.util.List;

import exception.UnexpectedVoidType;
import misc.Type;
import asm.Command;
import asm.com.Call;
import asm.com.Mov;
import asm.com.PopNull;
import asm.com.Push;
import asm.mem.CpuRegister;
import asm.mem.Memory;
import asm.mem.RWMemory;
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

    public static void subscribe(List<Command> programText, Memory src) {
        programText.add(new Push(src, null, null));
        programText.add(new Call("subscribe", null, null));
        programText.add(new PopNull(1, null, null));
    }

    public static void unsubscribe(List<Command> programText, Type type, Memory src) {
        programText.add(new Push(src, null, null));
        programText.add(new Call(Values.toString("unsubscribe", type), null, null));
        programText.add(new PopNull(1, null, null));
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
