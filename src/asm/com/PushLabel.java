package asm.com;

import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

import asm.Command;
import asm.State;
import asm.mem.Memory;

public class PushLabel extends Command {
    public final String src;

    public PushLabel(String src, String label, String comment) {
        super(label, comment);
        this.src = Objects.requireNonNull(src);
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        int value = state.getAddress(src);
        state.esp -= 4;
        state.setRam(state.esp, value);
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "push " + src;
    }

}
