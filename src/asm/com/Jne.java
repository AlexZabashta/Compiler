package asm.com;

import java.util.Objects;

import lex.Token;
import asm.Command;
import asm.State;

public class Jne extends Command {

    public final String target;

    public Jne(String target, String label, Token token) {
        super(label, token);
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public void execute(State state) {
        Integer eip = state.textLabels.get(target);
        if (eip == null) {
            throw new RuntimeException("Can't find label " + target);
        }

        if (state.cmp == 0) {
            state.eip++;
        } else {
            state.eip = eip;
        }
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "jne " + target;
    }

}
