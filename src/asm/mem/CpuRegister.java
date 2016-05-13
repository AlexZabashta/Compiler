package asm.mem;

import java.util.Objects;

import asm.Register;
import asm.State;

public class CpuRegister implements RWMemory {

    public final Register register;

    public CpuRegister(Register register) {
        this.register = Objects.requireNonNull(register);
    }

    public CpuRegister() {
        this(Register.EAX);
    }

    @Override
    public String toStringYASM_WIN_32() {
        return register.toString().toLowerCase();
    }

    @Override
    public int get(State state) {
        return state.register[register.ordinal()];
    }

    @Override
    public void set(State state, int value) {
        state.register[register.ordinal()] = value;
    }

    @Override
    public boolean useRam() {
        return false;
    }

}
