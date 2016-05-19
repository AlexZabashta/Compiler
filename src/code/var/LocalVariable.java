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

    public int counter = 0;

    public int offset = 0;
    public Register register = null;
    public final VisibilityZone visibilityZone;

    @Override
    public void use(int cnt) {
        counter += cnt;
    }

    public LocalVariable(Type type, VisibilityZone visibilityZone, int offset) throws UnexpectedVoidType {
        super(type);
        this.visibilityZone = visibilityZone;
        if (offset < 0) {
            throw new RuntimeException("offset < 0");
        }
        this.offset = offset;
    }

    public RWMemory rwMemory() {
        if (register == null) {
            return new RamEsp(offset);
        } else {
            return new CpuRegister(register);
        }
    }

    @Override
    public String toString() {
        return "<" + code + ":" + type + ">";
    }

}
