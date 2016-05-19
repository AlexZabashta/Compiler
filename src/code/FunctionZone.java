package code;

import java.util.List;

import asm.Command;
import ast.Function;
import code.var.LocalVariable;
import exception.UnexpectedVoidType;

public class FunctionZone extends VisibilityZone {

    public LocalVariable result;

    public FunctionZone(Function function) {
        super(function.toString(), function.name.toString());
        super.root = this;

        try {
            this.result = new LocalVariable(function.type, this, -1);
        } catch (UnexpectedVoidType e) {
            this.result = null;
        }
    }

    @Override
    public void asm(List<Command> programText) {
        end();
        programText.add(start());
        // TODO SAVE REGISTERS

        for (Action action : actions) {
            action.asm(programText);
        }

        programText.add(new asm.com.Ret(null, null));

    }

}
