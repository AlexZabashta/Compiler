package asm;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import asm.com.Mov;
import asm.com.Nop;
import asm.com.Xor;
import asm.mem.ConstInt;
import asm.mem.RWMemory;

public abstract class Command extends AsmLine {

    public String target;

    public Command(String label, String target, String comment) {
        super(label, comment);
        this.target = target;
    }

    public Command optimize() {
        return this;
    }

    public abstract void execute(State state, Reader input, Writer output) throws IOException;

    public Command setConstInt(RWMemory dst, ConstInt constInt) {
        if (constInt.value == 0) {
            return setZero(dst);
        } else {
            return new Mov(dst, constInt, label, comment);
        }
    }

    public Command nop() {
        return new Nop(label, comment);
    }

    public Command setZero(RWMemory dst) {
        if (dst.useRam()) {
            return new Mov(dst, new ConstInt(0), label, comment);
        } else {
            return new Xor(dst, dst, label, comment);
        }
    }

}
