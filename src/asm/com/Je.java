package asm.com;

import java.util.Objects;

import asm.Command;
import asm.State;

public class Je extends Command {

    public final String target;

    public Je(String target, String label, String comment) {
        super(label, comment);
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public void execute(State state) {
        Integer eip = state.textLabels.get(target);
        if (eip == null) {
            throw new RuntimeException("Can't find label " + target);
        }

        if (state.cmp == 0) {
            state.eip = eip;
        } else {
            state.eip++;
        }
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "je " + target;
    }

}
