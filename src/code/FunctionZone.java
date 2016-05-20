package code;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import asm.Command;
import asm.Register;
import asm.com.ShiftEsp;
import asm.mem.CpuRegister;
import asm.mem.RamEsp;
import ast.Function;
import code.act.Nop;
import code.var.LocalVariable;
import code.var.Variable;
import exception.UnexpectedVoidType;
import lex.token.fold.DeclarationToken;

public class FunctionZone extends VisibilityZone {

    public LocalVariable result;
    public final Map<Register, Stack<LocalVariable>> reAlloc = new HashMap<>();

    public FunctionZone(Function function) {
        super(function.toString(), function.name.toString());
        super.root = this;

        for (Register register : registers) {
            reAlloc.put(register, new Stack<>());
        }
    }

    public String asmFunction(List<Command> programText) {
        asm(programText);
        programText.add(new asm.com.Ret(null, null));
        return label;
    }

    @Override
    public void shiftStack(int offset) {
        for (LocalVariable variable : vars) {
            variable.offset += offset;
        }
    }

    @Override
    public void asm(List<Command> programText) {
        Nop end = end();
        programText.add(start());

        if (result != null) {
            programText.add(new ShiftEsp(-1, null, null));
        }

        for (LocalVariable variable : vars) {
            if (variable == result) {
                Variable.init(programText, variable.type, variable.rwMemory());
            } else {
                Variable.subscribe(programText, variable.type, variable.rwMemory());
            }
        }

        for (Action action : actions) {
            action.asm(programText);
        }

        for (LocalVariable variable : vars) {
            if (variable != result) {
                Variable.unsubscribe(programText, variable.type, variable.rwMemory());
            }
        }

        if (result != null) {
            Variable.moveMem(programText, new CpuRegister(), result.memory());
            programText.add(new ShiftEsp(1, null, null));
        }

        programText.add(new asm.com.Nop(end.label, end.comment));
    }

}
