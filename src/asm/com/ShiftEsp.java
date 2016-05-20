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
    public Command optimize() {
        if (offset == 0) {
            return nop();
        }
        return this;
    }

    @Override
    public String toStringYASM_WIN_32() {

        if (offset < 0) {
            return "sub esp, " + (-offset);
        } else {
            return "add esp, " + offset;
        }

    }

}
