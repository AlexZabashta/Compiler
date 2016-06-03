package code;

import java.util.List;

import asm.Command;
import ast.Function;
import code.var.GlobalVariable;
import code.var.Variable;

public class InitFunctionZone extends FunctionZone {

    public final List<GlobalVariable> globalVariables;

    public InitFunctionZone(List<GlobalVariable> globalVariables, Function function) {
        super(function);
        this.globalVariables = globalVariables;
    }

    @Override
    public String asm(List<Command> programText) {
        super.asm(programText);
        programText.remove(programText.size() - 1);

        for (GlobalVariable variable : globalVariables) {
            Variable.unsubscribe(programText, variable.type, variable.memory());
        }

        programText.add(new asm.com.Ret(null, null));
        return label;

    }

}
