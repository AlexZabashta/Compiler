package ast.node;

import java.util.List;

import ast.Node;
import code.Environment;
import code.Variable;
import code.VisibilityZone;

public interface LValue extends Node {

    public void lValue(Variable src, VisibilityZone z, Environment e, List<String> errors);

}
