package asm.com;

import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.State;
import asm.mem.ConstInt;
import asm.mem.CpuRegister;
import asm.mem.Memory;
import asm.mem.RWMemory;
import code.var.ConstVariable;

public class Mov extends Command {

    public final RWMemory dst;
    public final Memory src;

    public Mov(RWMemory dst, Memory src, String label, String comment) {
        super(label, null, comment);
        this.dst = dst;
        this.src = src;
        if (src.useRam() && dst.useRam()) {
            throw new RuntimeException("Double ram memory usage");
        }
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        int value = src.get(state);
        dst.set(state, value);
        state.eip++;
    }

    @Override
    public Command optimize() {
        if (dst.equals(src)) {
            return new Nop(label, comment);
        }

        if (dst.equals(ConstVariable.FALSE)) {
            return setZero(dst);
        }

        return this;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "mov " + dst.toStringYASM_WIN_32() + ", " + src.toStringYASM_WIN_32();
    }

}
