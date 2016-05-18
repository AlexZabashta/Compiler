package code.var;

import misc.Type;
import asm.Register;
import asm.mem.CpuRegister;
import asm.mem.RWMemory;
import asm.mem.RamEsp;
import code.VisibilityZone;
import exception.UnexpectedVoidType;

public class LocalVariable extends Variable {
    private static int codeCnt = 0;
    private final int code = ++codeCnt;
    private int offset = 0;

    private Register register = null;
    public final VisibilityZone visibilityZone;

    public LocalVariable(Type type, VisibilityZone visibilityZone, int offset) throws UnexpectedVoidType {
        super(type);
        this.visibilityZone = visibilityZone;
        this.offset = offset;
    }

    public RWMemory rwMemory() {
        if (register == null) {
            return new CpuRegister(register);
        } else {
            return new RamEsp(offset * 4);
        }
    }

    @Override
    public String toString() {
        return "<" + code + ":" + type + ">";
    }

}
