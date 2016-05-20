package asm.mem;

import java.util.List;
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
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RamLabel other = (RamLabel) obj;
        return label.equals(other.label);
    }

    @Override
    public boolean useRam() {
        return true;
    }

    @Override
    public void getLabels(List<String> labels) {
        labels.add(label);
    }

}
