package asm.com;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.Register;
import asm.State;

public class CallGetChar extends Command {

    public CallGetChar(String label, String comment) {
        super(label, null, comment);
    }

    @Override
    public void execute(State state, Reader input, Writer output) throws IOException {
        int character = input.read();
        state.setReg(Register.EAX, character);
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "call _getchar";
    }

}
