package asm.com;

import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.State;

public class CallFree extends Command {

    public CallFree(String label, String comment) {
        super(label, null, comment);
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
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
