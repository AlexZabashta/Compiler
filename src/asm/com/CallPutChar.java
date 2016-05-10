package asm.com;

import java.io.IOException;

import lex.Token;
import asm.Command;
import asm.State;

public class CallPutChar extends Command {

    public CallPutChar(String label, Token token) {
        super(label, token);
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
