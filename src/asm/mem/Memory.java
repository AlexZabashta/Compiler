package asm.mem;

import java.util.List;

import asm.State;

public interface Memory {
    String toStringYASM_WIN_32();

    int get(State state);

    boolean useRam();

    public void getLabels(List<String> labels);
}
