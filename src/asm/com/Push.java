package asm.com;

import java.util.Objects;

import asm.Command;
import asm.State;
import asm.mem.Memory;

public class Push extends Command {
    public final Memory src;

    public Push(Memory src, String label, String comment) {
        super(label, comment);
        this.src = Objects.requireNonNull(src);
    }

    @Override
    public void execute(State state) {
        int value = src.get(state);
        state.esp -= 4;
        state.setRam(state.esp, value);
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "push " + src.toStringYASM_WIN_32();
    }

}
