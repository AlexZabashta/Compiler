package asm.com;

import java.io.IOException;

import asm.Command;
import asm.State;

public class CallPutChar extends Command {

    public CallPutChar(String label, String comment) {
        super(label, comment);
    }

    @Override
    public void execute(State state) {
        int characterPtr = state.esp;
        int character = state.getRam(characterPtr);
        try {
            state.output.write(character);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "call _putchar";
    }

}
