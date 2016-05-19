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

}
