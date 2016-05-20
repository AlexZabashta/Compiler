package asm.mem;

import java.util.List;

import asm.State;

public class Label implements Memory {

    public String label;

    public Label(String label) {
        this.label = label;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "dword " + label;
    }

    @Override
    public int get(State state) {
        return state.getAddress(label);
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
        Label other = (Label) obj;
        return label.equals(other.label);
    }

    @Override
    public boolean useRam() {
        return false;
    }

    @Override
    public void getLabels(List<String> labels) {
        labels.add(label);

    }

}
