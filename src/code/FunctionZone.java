package code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import asm.Command;
import asm.Register;
import ast.Function;
import code.var.LocalVariable;
import exception.UnexpectedVoidType;

public class FunctionZone extends VisibilityZone {

    public LocalVariable result;

    public final Map<Register, Stack<LocalVariable>> reAlloc = new HashMap<>();

    public FunctionZone(Function function) {
        super(function.toString(), function.name.toString());
        super.root = this;

        for (Register register : registers) {
            reAlloc.put(register, new Stack<>());
        }

        try {
            this.result = new LocalVariable(function.type, this, 0);
        } catch (UnexpectedVoidType e) {
            this.result = null;
        }
    }

    public String asmFunction(List<Command> programText) {
        asm(programText);
        programText.add(new asm.com.Ret(null, null));
        return label;
    }

    @Override
    public void pop(int offset) {
        super.pop(offset);
    }

    @Override
    public void push(int offset) {
        super.push(offset);
    }

    @Override
    public void asm(List<Command> programText) {

    }

}
