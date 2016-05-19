package asm.mem;

import java.util.List;
import java.util.Objects;

import asm.Register;
import asm.State;

public class RamEsp implements RWMemory {

    public final int offset;

    public RamEsp(int integerOffset) {
        if (integerOffset < 0) {
            throw new RuntimeException("integerOffset = " + integerOffset + " < 0");
        }
        this.offset = integerOffset * 4;
    }

    @Override
    public String toStringYASM_WIN_32() {
        if (offset == 0) {
            return "dword [esp]";
        } else {
            return "dword [esp + " + offset + "]";
        }
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

    @Override
    public void getLabels(List<String> labels) {

    }

}
