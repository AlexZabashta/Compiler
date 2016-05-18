package asm.com;

import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

import asm.Command;
import asm.State;

public class Call extends Command {

    public String target;

    public Call(String target, String label, String comment) {
        super(label, comment);
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        Integer eip = state.textLabels.get(target);
        if (eip == null) {
            throw new RuntimeException("Can't find label " + target);
        }
        state.esp -= 4;
        state.setRam(state.esp, state.eip);
        state.eip = eip + 1;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "call " + target;
    }

}
