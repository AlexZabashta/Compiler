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
    public boolean useRam() {
        return false;
    }

    @Override
    public void getLabels(List<String> labels) {
        labels.add(label);

    }

}
