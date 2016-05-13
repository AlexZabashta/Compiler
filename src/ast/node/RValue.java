package ast.node;

import ast.Node;
import misc.Type;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import exception.Log;
import exception.ParseException;

public interface RValue extends Node {

    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException;

    public Type type(Environment e);
}
