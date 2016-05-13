package asm.com;

import java.util.Objects;

import asm.Command;
import asm.State;
import asm.mem.RWMemory;

public class Pop extends Command {
    public final RWMemory dst;

    public Pop(RWMemory dst, String label, String comment) {
        super(label, comment);
        this.dst = Objects.requireNonNull(dst);
    }

    @Override
    public void execute(State state) {
        int value = state.getRam(state.esp);
        state.esp += 4;
        dst.set(state, value);
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "pop " + dst.toStringYASM_WIN_32();
    }

}
