package asm.com;

import asm.Command;
import asm.State;

public class CallFree extends Command {

    public CallFree(String label, String comment) {
        super(label, comment);
    }

    @Override
    public void execute(State state) {
        int adressPtr = state.esp;
        int address = state.getRam(adressPtr);
        state.free(address);
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "call _free";
    }

}
