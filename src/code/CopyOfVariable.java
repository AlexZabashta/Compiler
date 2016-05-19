package code;

import java.util.List;

import misc.Type;
import asm.Command;
import asm.com.Call;
import asm.com.Mov;
import asm.com.ShiftEsp;
import asm.com.Push;
import asm.mem.CpuRegister;
import asm.mem.Memory;
import asm.mem.RWMemory;
import asm.mem.RamEsp;
import ast.node.Values;

public class CopyOfVariable {
    public static void moveMem(List<Command> programText, RWMemory dst, Memory src) {
        if (src.useRam() && dst.useRam()) {
            RWMemory eax = new CpuRegister();
            programText.add(new Mov(eax, src, null, null));
            programText.add(new Mov(dst, eax, null, null));
        } else {
            programText.add(new Mov(dst, src, null, null));
        }
    }
    public static void moveVar(List<Command> programText, Type type, RWMemory dst, Memory src) {
        if (type.idVoid()) {
            throw new RuntimeException("Can't use void type as variable");
        }
        if (dst.equals(src)) {
            return;
        }
        if (type.dim == 0) {
            moveMem(programText, dst, src);
        } else {
            subscribe(programText, src);
            unsubscribe(programText, type, dst);
            moveMem(programText, dst, src);
        }
    }
    public static void subscribe(List<Command> programText, Memory src) {
        programText.add(new Push(src, null, null));
        programText.add(new Call("subscribe", null, null));
        programText.add(new ShiftEsp(1, null, null));
    }

    public static void unsubscribe(List<Command> programText, Type type, Memory src) {
        programText.add(new Push(src, null, null));
        programText.add(new Call(Values.toString("unsubscribe", type), null, null));
        programText.add(new ShiftEsp(1, null, null));
    }

    public final int index;

    public String location = "where you want"; // TODO

    public final Type type;

    public final VisibilityZone visibilityZone;

    public CopyOfVariable(Type type, String location) {
        if (type.idVoid()) {
            throw new RuntimeException("Can't create void variable");
        }
        this.type = type;
        this.location = location;

        this.visibilityZone = null;
        this.index = 0;
    }

    public CopyOfVariable(Type type, VisibilityZone visibilityZone, int index) {
        if (type.idVoid()) {
            throw new RuntimeException("Can't create void variable");
        }
        this.type = type;
        this.visibilityZone = visibilityZone;
        this.index = index;
    }

    public int distance(VisibilityZone from) {
        VisibilityZone to = visibilityZone, cur = from;
        int distance = 0;

        while (cur != null) {
            if (to == cur) {
                if (cur.parent == null) {
                    distance += 1;
                }
                distance += cur.numberOfVars() - index - 1;
                break;
            }
            distance += cur.numberOfVars();
            cur = cur.parent;
        }

        if (cur == null) {
            throw new RuntimeException("Can't find " + this + " from " + from);
        }

        return distance;
    }

    public RWMemory memory() {
        // TODO ADD distance
        return new RamEsp(index);
    }

    @Override
    public String toString() {
        return "<" + visibilityZone.label + " " + index + " " + type + ">";
    }

}
