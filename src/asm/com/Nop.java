package asm.com;

import lex.Token;
import asm.Command;
import asm.State;

public class Nop extends Command {

    public Nop(String label, Token token) {
        super(label, token);
    }

    @Override
    public void execute(State state) {
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "nop";
    }

}
