package code;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import code.var.LocalVariable;
import exception.DeclarationException;
import exception.UnexpectedVoidType;
import lex.token.fold.DeclarationToken;
import misc.Type;

public class VisibilityZone {

    protected final List<Action> actions = new ArrayList<Action>();
    private final List<String> decVars = new ArrayList<String>();

    public final List<LocalVariable> vars = new ArrayList<LocalVariable>();

    public void addAction(Action action) {
        actions.add(action);
    }

    public void addZone(VisibilityZone zone, Environment environment) throws DeclarationException {
        this.actions.addAll(zone.actions);
        this.vars.addAll(zone.vars);
        zone.removeAll(environment);
    }

    public LocalVariable createVariable(DeclarationToken token, Environment environment) throws UnexpectedVoidType, DeclarationException {

        if (token.varToken.pac != null) {
            throw new DeclarationException("Can't declare global varible here");
        }

        String name = token.varToken.name.toTokenString();
        LocalVariable var = createVariable(token.typeToken.type);

        environment.addLocalVariable(name, var);

        decVars.add(name);
        return var;
    }

    public LocalVariable createVariable(Type type) throws UnexpectedVoidType {
        if (type.idVoid()) {
            throw new UnexpectedVoidType("Can't declare void variable");
        }
        LocalVariable variable = new LocalVariable(type);
        vars.add(variable);
        return variable;
    }

    public void removeAll(Environment environment) throws DeclarationException {
        for (String var : decVars) {
            environment.removeLocalVariable(var);
        }
    }

}
