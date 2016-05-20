package asm.mem;

import java.util.List;
import java.util.Objects;

import asm.Register;
import asm.State;

public class RamRegister implements RWMemory {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + offset;
        result = prime * result + register.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RamRegister other = (RamRegister) obj;
        if (offset != other.offset)
            return false;
        if (register != other.register)
            return false;
        return true;
    }

    public final Register register;
    public final int offset;

    public RamRegister(Register register) {
        this(register, 0);
    }

    public RamRegister(Register register, int integerOffset) {
        this.register = Objects.requireNonNull(register);
        this.offset = 4 * integerOffset;
    }

    @Override
    public String toStringYASM_WIN_32() {
        if (offset == 0) {
            return "dword [" + register.toString().toLowerCase() + "]";
        } else {
            if (offset < 0) {
                return "dword [" + register.toString().toLowerCase() + " - " + (-offset) + "]";
            } else {
                return "dword [" + register.toString().toLowerCase() + " + " + offset + "]";
            }
        }
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

    @Override
    public void getLabels(List<String> labels) {

    }

}
