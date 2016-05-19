package asm.mem;

import java.util.Objects;

import asm.State;

public class RamLabel implements RWMemory {

    public String label;

    public RamLabel(String label) {
        this.label = label;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "dword [" + label + "]";
    }

    @Override
    public int get(State state) {
        return state.getRam(label);
    }

    @Override
    public void set(State state, int value) {
        state.setRam(label, value);
    }

    @Override
    public boolean useRam() {
        return true;
    }

}
