package asm.mem;

import asm.State;

public interface Memory {
    String toStringYASM_WIN_32();

    int get(State state);

    boolean useRam();

}
