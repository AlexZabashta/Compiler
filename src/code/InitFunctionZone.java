package code;

import java.util.List;

import asm.Command;
import ast.Function;
import code.act.Nop;
import code.var.GlobalVariable;
import code.var.Variable;

public class InitFunctionZone extends FunctionZone {

    public final List<GlobalVariable> globalVariables;

    public InitFunctionZone(List<GlobalVariable> globalVariables, Function function) {
        super(function);
        this.globalVariables = globalVariables;
    }

    @Override
    public String asmFunction(List<Command> programText) {
        asm(programText);
        programText.add(new asm.com.Ret(null, null));
        return label;
    }

    @Override
    public void asm(List<Command> programText) {
        Nop end = end();
        programText.add(start());
        for (Action action : actions) {
            action.asm(programText);
        }

        for (GlobalVariable variable : globalVariables) {
            Variable.unsubscribe(programText, variable.type, variable.memory());
        }
        programText.add(new asm.com.Nop(end.label, end.comment));
    }

}
