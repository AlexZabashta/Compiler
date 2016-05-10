package asm.com;

import lex.Token;
import asm.Command;
import asm.State;

public class CallFree extends Command {

    public CallFree(String label, Token token) {
        super(label, token);
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
