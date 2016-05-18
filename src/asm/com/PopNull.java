package asm.com;

import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.State;

public class PopNull extends Command {
    public final int num;

    public PopNull(int num, String label, String comment) {
        super(label, comment);
        if (num < 0) {
            throw new RuntimeException("num = " + num + " < 0");
        }
        this.num = num * 4;
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        state.esp += num;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "add esp, " + num;
    }

}
