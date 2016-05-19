package asm.com;

import java.io.Reader;
import java.io.Writer;

import asm.Command;
import asm.Register;
import asm.State;
import asm.mem.ConstInt;
import asm.mem.Memory;
import asm.mem.RWMemory;

public class IMul extends Command {

    public final Memory src;

    public IMul(Memory src, String label, String comment) {
        super(label, null, comment);
        this.src = src;
        if (src instanceof ConstInt) {
            throw new RuntimeException("Can't multiple on const");
        }
    }

    @Override
    public void execute(State state, Reader input, Writer output) {
        int a = state.getReg(Register.EAX);
        int b = src.get(state);

        state.setReg(Register.EAX, a * b);

        state.eip++;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "imul " + src.toStringYASM_WIN_32();
    }

}
