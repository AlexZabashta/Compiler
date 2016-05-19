package asm.com;

import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.State;

public class PopNull extends Command {
    public final int offset;

    public PopNull(int integerOffset, String label, String comment) {
        super(label, comment);
        if (integerOffset < 0) {
            throw new RuntimeException("integerOffset = " + integerOffset + " < 0");
        }
        this.offset = integerOffset * 4;
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        state.esp += offset;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "add esp, " + offset;
    }

}
