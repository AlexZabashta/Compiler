package asm.com;

import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

import asm.Command;
import asm.State;

public class Jne extends Command {

    public Jne(String target, String label, String comment) {
        super(label, Objects.requireNonNull(target), comment);
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        Integer eip = state.textLabels.get(target);
        if (eip == null) {
            throw new RuntimeException("Can't find label " + target);
        }

        if (state.cmp == 0) {
            state.eip++;
        } else {
            state.eip = eip;
        }
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "jne " + target;
    }

}
