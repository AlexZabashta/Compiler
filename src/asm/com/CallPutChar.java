package asm.com;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.State;

public class CallPutChar extends Command {

    public CallPutChar(String label, String comment) {
        super(label, null, comment);
    }

    @Override
    public void execute(State state, Reader input, Writer output) throws IOException {
        int characterPtr = state.esp;
        int character = state.getRam(characterPtr);
        output.write(character);
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "call _putchar";
    }

}
