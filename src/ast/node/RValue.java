package ast.node;

import java.util.List;

import ast.Node;
import misc.Type;
import code.Environment;
import code.Variable;
import code.VisibilityZone;

public interface RValue extends Node {

    public void rValue(Variable dst, VisibilityZone z, Environment e, List<String> errors);

    public Type type(Environment e);
}
