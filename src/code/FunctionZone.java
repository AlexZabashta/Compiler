package code;

import java.util.List;

import ast.Function;

public class FunctionZone extends VisibilityZone {

    public final Variable result;

    public FunctionZone(Function function) {
        super(function.toString(), function.name);
        super.root = this;
        this.result = new Variable(function.type, this, -1);
    }

    @Override
    public void asm(List<String> programText, List<String> errors) {
        end();
        programText.add(label() + ":" + comment());

        for (Action action : actions) {
            action.asm(programText, errors);
        }
    }

}
