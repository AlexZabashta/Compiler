package asm.com;

import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.Register;
import asm.State;

public class CallMalloc extends Command {

    public CallMalloc(String label, String comment) {
        super(label, comment);
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        int sizePtr = state.esp;
        int size = state.getRam(sizePtr);
        int address = state.malloc(size);
        state.setReg(Register.EAX, address);
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "call _malloc";
    }

}
