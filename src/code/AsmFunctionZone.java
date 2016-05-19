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
    public void asm(List<Command> programText) {
        programText.add(start());
        for (Command command : asmCode) {
            programText.add(command);
        }
    }

    @Override
    public void printDensely(PrintWriter out, int indent) {
        println(out, indent);
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println();

        for (Command command : asmCode) {
            command.printYASM_WIN_32(out, indent + 1);
        }
    }
}
