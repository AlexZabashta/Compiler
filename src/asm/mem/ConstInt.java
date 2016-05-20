package asm.mem;

import java.util.List;
import java.util.Objects;

import asm.State;

public class ConstInt implements Memory {

    public final int value;

    public ConstInt(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConstInt other = (ConstInt) obj;
        return value == other.value;
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
