package asm.com;

import asm.Command;
import asm.State;

public class Nop extends Command {

    public Nop(String label, String comment) {
        super(label, comment);
    }

    @Override
    public void execute(State state) {
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "nop";
    }

}
