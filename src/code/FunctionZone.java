package code;

import ast.Function;

public class FunctionZone extends VisibilityZone {

    public final Variable result;

    public FunctionZone(Function function) {
        super(function.toString(), function.name);
        super.root = this;

        this.result = new Variable(function.type, this, -1);
    }

}
