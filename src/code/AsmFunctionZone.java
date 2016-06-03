package code;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import ast.Function;
import code.var.LocalVariable;
import exception.UnexpectedVoidType;

public class AsmFunctionZone extends FunctionZone {

    public final List<Command> asmCode;

    public AsmFunctionZone(List<Command> asmCode, Function function) {
        super(function);
        this.asmCode = asmCode;
    }

    @Override
    public String asm(List<Command> programText) {
        programText.add(new asm.com.Nop(label, null));
        for (Command command : asmCode) {
            programText.add(command);
        }
        programText.add(new asm.com.Ret(null, null));
        return label;
    }

    @Override
    public void println(PrintWriter out, int indent) {
        for (Command command : asmCode) {
            command.printYASM_WIN_32(out, indent + 1);
        }
    }
}
