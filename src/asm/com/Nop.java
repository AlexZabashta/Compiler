package asm.com;

import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.State;

public class Nop extends Command {

    public Nop(String label, String comment) {
        super(label, null,comment);
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "nop";
    }

}
