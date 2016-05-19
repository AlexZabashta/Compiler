package asm.com;

import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.State;

public class ShiftEsp extends Command {
    public final int offset;

    public ShiftEsp(int integerOffset, String label, String comment) {
        super(label, null, comment);
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
