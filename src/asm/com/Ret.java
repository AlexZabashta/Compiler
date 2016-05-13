package asm.com;

import asm.Command;
import asm.State;

public class Ret extends Command {

    public Ret(String label, String comment) {
        super(label, comment);

    }

    @Override
    public void execute(State state) {
        state.eip = state.getRam(state.esp);
        state.esp += 4;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "ret";
    }

}
