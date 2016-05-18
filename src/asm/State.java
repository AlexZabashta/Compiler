package asm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class State {

    public static final int PAGE_SIZE = 1024;

    public final int numberOfPages = 1024;

    public final Map<String, Integer> dataLabels = new HashMap<String, Integer>();

    public int esp, eip, cmp;

    public final Stack<Integer> freePages = new Stack<Integer>();

    public final Set<String> labels = new HashSet<String>();

    public final int[][] memory;
    public final boolean[] staticMemory;
    public final int[] register = new int[Register.values().length];
    public final Map<String, Integer> textLabels = new HashMap<String, Integer>();

    public State() {
        this.memory = new int[numberOfPages][];
        this.staticMemory = new boolean[numberOfPages];

        for (int i = 0; i < numberOfPages; i++) {
            freePages.add(i);
        }
    }

    public int align(int address) {
        if (address % 4 != 0) {
            throw new RuntimeException("align = " + address % 4 + " != 4");
        }
        return address / 4;
    }

    public int getAddress(String label) {
        Integer pid = dataLabels.get(label);
        if (pid == null) {
            throw new RuntimeException("Can't find label " + label);
        }
        return pid * PAGE_SIZE;
    }

    public int getEIP(String label) {
        Integer eip = textLabels.get(label);
        if (eip == null) {
            throw new RuntimeException("Can't find label " + label);
        }
        return eip;
    }

    public int getRam(int address) {
        return memory[address / PAGE_SIZE][align(address % PAGE_SIZE)];
    }

    public int getRam(String label) {
        return getRam(getAddress(label));
    }

    public int getReg(Register register) {
        return this.register[register.ordinal()];
    }

    public int malloc(int size) {
        int len = (size + 3) / 4;

        if (4 * len > PAGE_SIZE) {
            throw new RuntimeException("Page length too long");
        }

        while (!freePages.isEmpty()) {
            int pid = freePages.pop();
            if (memory[pid] == null) {
                memory[pid] = new int[len];
                return pid * PAGE_SIZE;
            }
        }

        throw new RuntimeException("Memory limit exceed");
    }

    public void free(int address) {
        if (address % PAGE_SIZE != 0) {
            throw new RuntimeException("Wrong address align " + address % PAGE_SIZE);
        }
        int pid = address / PAGE_SIZE;
        if (memory[pid] == null) {
            throw new RuntimeException("Page " + pid + " not alloced yet");
        }

        if (staticMemory[pid]) {
            throw new RuntimeException("Can't free static memory");
        }

        memory[pid] = null;
        freePages.add(pid);
    }

    public void setRam(int address, int val) {
        memory[address / PAGE_SIZE][align(address % PAGE_SIZE)] = val;
    }

    public void setRam(String label, int val) {
        setRam(getAddress(label), val);
    }

    public void setReg(Register register, int val) {
        this.register[register.ordinal()] = val;
    }

}
