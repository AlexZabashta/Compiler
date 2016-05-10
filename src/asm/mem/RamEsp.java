package asm.mem;

import java.util.Objects;

import asm.Register;
import asm.State;

public class RamEsp implements RWMemory {

    public final int offset;

    public RamEsp(int offset) {
        this.offset = offset;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "[esp + " + offset + "]";
    }

    @Override
    public int get(State state) {
        int address = state.esp + offset;
        return state.getRam(address);
    }

    @Override
    public void set(State state, int value) {
        int address = state.esp;
        state.setRam(address, value);
    }

    @Override
    public boolean useRam() {
        return true;
    }

}
