package asm.mem;

import java.util.Objects;

import asm.Register;
import asm.State;

public class RamRegister implements RWMemory {

    public final Register register;
    public final int offset;

    public RamRegister(Register register) {
        this(register, 0);
    }

    public RamRegister(Register register, int offset) {
        this.register = Objects.requireNonNull(register);
        this.offset = offset;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return "[" + register.toString().toLowerCase() + " + " + offset + "]";
    }

    @Override
    public int get(State state) {
        int address = state.getReg(register);
        return state.getRam(address);
    }

    @Override
    public void set(State state, int value) {
        int address = state.getReg(register);
        state.setRam(address, value);
    }

    @Override
    public boolean useRam() {
        return true;
    }

}
