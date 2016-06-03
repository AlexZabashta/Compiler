package code;

import static asm.Register.EBP;
import static asm.Register.EBX;
import static asm.Register.ECX;
import static asm.Register.EDI;
import static asm.Register.EDX;
import static asm.Register.ESI;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import asm.Command;
import asm.Register;
import asm.com.Mov;
import asm.com.ShiftEsp;
import asm.mem.CpuRegister;
import asm.mem.RamEsp;
import ast.Function;
import code.act.Nop;
import code.var.LocalVariable;
import code.var.Variable;
import exception.DeclarationException;
import exception.SemanticException;
import exception.UnexpectedVoidType;
import lex.token.fold.DeclarationToken;

public class FunctionZone extends VisibilityZone {

    public static final Register[] registers = { EBX, ECX, EDX, EDI, ESI, EBP };
    public static int regPointer = 0;

    public static Register nextRegister() {
        return registers[(regPointer++) % registers.length];
    }

    public final String label;

    public final List<LocalVariable> args = new ArrayList<LocalVariable>();

    // public final Map<Register, Stack<LocalVariable>> reAlloc = new HashMap<>();
    public LocalVariable result;

    public FunctionZone(Function function) {
        super();
        label = function.toString();
    }

    public void printIndent(PrintWriter out, int indent) {
        while (--indent >= 0) {
            out.print("    ");
        }
    }

    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(label);
        printIndent(out, indent);
        out.print(args);

        if (result != null) {
            out.print(" = " + result);
        }
        out.println();

        for (Action action : actions) {
            action.println(out, indent + 1);
        }
    }

    public String asm(List<Command> programText) {
        for (Action action : actions) {
            action.parent = this;
        }
        programText.add(new asm.com.Nop(label, null));
        {
            {// ARGS
                int alen = args.size();

                for (int i = 0; i < alen; i++) {
                    LocalVariable variable = args.get(i);
                    variable.offset = alen - i;
                    Variable.subscribe(programText, variable.type, variable.rwMemory());
                }

                if (result != null) {
                    shiftStack(1, programText);
                    result.offset = 0;
                    Variable.init(programText, result.type, result.rwMemory());
                }

                { // LocalVars

                    int len = vars.size();

                    Collections.sort(vars, new Comparator<LocalVariable>() {
                        @Override
                        public int compare(LocalVariable v, LocalVariable u) {
                            return Integer.compare(u.counter, v.counter);
                        }
                    });

                    int reg = Math.min(len, registers.length);

                    shiftStack(len, programText);

                    for (int i = 0; i < len; i++) {
                        LocalVariable variable = vars.get(i);
                        variable.offset = len - i - 1;
                        if (i < reg) {
                            Register register = registers[i];
                            variable.register = register;
                            Variable.moveMem(programText, new RamEsp(variable.offset), new CpuRegister(register));
                        } else {
                            variable.register = null;
                        }
                        Variable.init(programText, variable.type, variable.rwMemory());
                    }

                    for (Action action : actions) {
                        action.asm(programText);
                    }

                    for (int i = 0; i < len; i++) {
                        LocalVariable variable = vars.get(i);
                        Variable.unsubscribe(programText, variable.type, variable.rwMemory());
                        if (i < reg) {
                            Register register = variable.register;
                            int offset = len - i - 1;
                            Variable.moveMem(programText, new CpuRegister(register), new RamEsp(offset));
                        }
                    }

                    shiftStack(-len, programText);
                }

                for (Variable variable : args) {
                    Variable.unsubscribe(programText, variable.type, variable.rwMemory());
                }

                if (result != null) {
                    programText.add(new Mov(new CpuRegister(), result.memory(), null, null));
                    shiftStack(-1, programText);
                }

            }

        }
        programText.add(new asm.com.Ret(null, null));
        return label;
    }

    public void shiftStack(int offset, List<Command> programText) {
        programText.add(new ShiftEsp(-offset, null, null));
        shiftStack(offset);
    }

    public void shiftStack(int offset) {
        for (LocalVariable variable : vars) {
            variable.offset += offset;
        }

        for (LocalVariable variable : args) {
            variable.offset += offset;
        }

        if (result != null) {
            result.offset += offset;
        }
    }

}
