package asm.com;

import lex.Token;
import asm.Command;
import asm.State;
import asm.mem.Memory;
import asm.mem.RWMemory;

public class Mov extends Command {

    public final RWMemory dst;
    public final Memory src;

    public Mov(RWMemory dst, Memory src, String label, Token token) {
        super(label, token);
        this.dst = dst;
        this.src = src;
        if (src.useRam() && dst.useRam()) {
            throw new RuntimeException("Double ram memory usage");
        }
    }

    @Override
    public void execute(State state) {
        int value = src.get(state);
        dst.set(state, value);
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "mov " + dst.toStringYASM_WIN_32() + ", " + src.toStringYASM_WIN_32();
    }

}