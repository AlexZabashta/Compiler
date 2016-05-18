package code;

import java.util.HashMap;
import java.util.Map;

import ast.Function;
import code.var.GlobalVariable;
import code.var.LocalVariable;
import exception.DeclarationException;

public class Environment {
    private final Map<String, Function> f;
    private final Map<String, GlobalVariable> gv;
    private final Map<String, LocalVariable> lv;

    public void removeLocalVariable(String name) throws DeclarationException {
        if (lv.remove(name) == null) {
            throw new DeclarationException("Can't find " + name + " declaration");
        }
    }

    public void addGlobalVar(String name, GlobalVariable variable) throws DeclarationException {
        if (gv.put(name, variable) != null) {
            throw new DeclarationException("Duplicate variable " + name + " declaration");
        }
    }

    public void addLocalVariable(String name, LocalVariable variable) throws DeclarationException {
        if (lv.put(name, variable) != null) {
            throw new DeclarationException("Duplicate variable " + name + " declaration");
        }
    }

    public void addFunction(String name, Function function) throws DeclarationException {
        if (f.put(name, function) != null) {
            throw new DeclarationException("Duplicate variable " + name + " declaration");
        }
    }

    public Environment(Map<String, GlobalVariable> gv, Map<String, Function> f) {
        this.lv = new HashMap<String, LocalVariable>();
        this.gv = gv;
        this.f = f;
    }

    public GlobalVariable globalVar(String name) throws DeclarationException {
        GlobalVariable variable = gv.get(name);

        if (variable == null) {
            throw new DeclarationException("Can't find " + name + " declaration");
        }

        return variable;
    }

    public LocalVariable localVar(String name) throws DeclarationException {
        LocalVariable variable = lv.get(name);

        if (variable == null) {
            throw new DeclarationException("Can't find " + name + " declaration");
        }

        return variable;
    }

    public Function function(String name) throws DeclarationException {
        Function function = f.get(name);

        if (function == null) {
            throw new DeclarationException("Can't find " + name + " declaration");
        }

        return function;
    }

}
