package ast.node;

import ast.Node;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import exception.Log;
import exception.ParseException;

public interface LValue extends Node {

    public void lValue(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException;

}
