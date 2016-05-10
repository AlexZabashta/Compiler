package asm.com;

import lex.Token;
import asm.Command;
import asm.State;
import asm.mem.Memory;

public class Cmp extends Command {

    public final Memory l, r;

    public Cmp(Memory l, Memory r, String label, Token token) {
        super(label, token);
        this.l = l;
        this.r = r;
        if (l.useRam() && r.useRam()) {
            throw new RuntimeException("Double ram memory usage");
        }
    }

    @Override
    public void execute(State state) {
        state.cmp = Integer.compare(l.get(state), r.get(state));
        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "cmp " + l.toStringYASM_WIN_32() + ", " + r.toStringYASM_WIN_32();
    }

}
