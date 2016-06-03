package ast.node;

import ast.Node;
import code.Environment;
import code.VisibilityZone;
import code.var.Variable;
import exception.Log;
import exception.ParseException;

public interface LValue extends Node {

    public void setVariable(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException;

}
