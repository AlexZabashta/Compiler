package asm.mem;

import asm.State;

public interface RWMemory extends Memory {

    void set(State state, int value);

}
