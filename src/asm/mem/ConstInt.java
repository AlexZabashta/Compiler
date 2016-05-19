package asm.mem;

import java.util.List;
import java.util.Objects;

import asm.State;

public class ConstInt implements Memory {

    public final int value;

    public ConstInt(int value) {
        this.value = value;
    }

    public ConstInt() {
        this(0);
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "dword " + value;
    }

    @Override
    public int get(State state) {
        return value;
    }

    @Override
    public boolean useRam() {
        return false;
    }

    @Override
    public void getLabels(List<String> labels) {

    }

}
