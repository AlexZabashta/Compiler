package asm.com;

import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.State;
import asm.mem.ConstInt;
import asm.mem.Memory;
import asm.mem.RWMemory;
import code.var.ConstVariable;

public class Sub extends Command {

    public final RWMemory dst;
    public final Memory src;

    public Sub(RWMemory dst, Memory src, String label, String comment) {
        super(label, null, comment);
        this.dst = dst;
        this.src = src;
        if (src.useRam() && dst.useRam()) {
            throw new RuntimeException("Double ram memory usage");
        }
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        int value = 0;
        value -= src.get(state);
        value += dst.get(state);
        dst.set(state, value);
        state.eip++;
    }

    @Override
    public Command optimize() {
        if (dst.equals(src)) {
            return setZero(dst);
        }

        if (src.equals(ConstVariable.FALSE)) {
            return nop();
        }

        return this;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "sub " + dst.toStringYASM_WIN_32() + ", " + src.toStringYASM_WIN_32();
    }

}
