package asm.com;

import java.io.IOException;

import asm.Command;
import asm.Register;
import asm.State;

public class CallGetChar extends Command {

    public CallGetChar(String label, String comment) {
        super(label, comment);
    }

    @Override
    public void execute(State state) {
        try {
            int character = state.input.read();
            state.setReg(Register.EAX, character);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "call _getchar";
    }

}
