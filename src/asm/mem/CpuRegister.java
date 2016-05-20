package asm.mem;

import java.util.List;
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
    public int hashCode() {
        return register.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CpuRegister other = (CpuRegister) obj;
        return register == other.register;
    }

    @Override
    public String toStringYASM_WIN_32() {
        return register.toString().toLowerCase();
    }

    @Override
    public int get(State state) {
        return state.getReg(register);
    }

    @Override
    public void set(State state, int value) {
        state.setReg(register, value);
    }

    @Override
    public boolean useRam() {
        return false;
    }

    @Override
    public void getLabels(List<String> labels) {

    }

}
