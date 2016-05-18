package ast.node;

import misc.Type;
import ast.Node;
import code.Environment;
import code.VisibilityZone;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;

public interface RValue extends Node {

    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException;

    public Type type(Environment e) throws DeclarationException;
}
