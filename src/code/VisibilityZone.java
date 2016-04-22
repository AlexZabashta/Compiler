package code;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lex.Token;
import lex.token.fold.DeclarationToken;
import misc.Type;

public class VisibilityZone extends Action {

    private final List<Action> actions = new ArrayList<Action>();
    private final List<String> decVars = new ArrayList<String>();
    public final int level;
    int numberOfVars = 0;
    public final VisibilityZone parent;
    protected FunctionZone root;

    private final List<Variable> vars = new ArrayList<Variable>();
    public final boolean visible;

    public VisibilityZone(String label, Token token) {
        super(label, token);
        this.level = 0;
        this.parent = null;
        this.visible = true;
    }

    public VisibilityZone(VisibilityZone parent, boolean visible, Token token) {
        super(token);
        this.level = parent.level + 1;
        this.visible = visible;
        this.parent = parent;
        this.root = parent.root;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public Variable createVariable(DeclarationToken token, Map<String, Variable> localVariables, List<String> errors) {
        if (token.varToken.pac != null) {
            errors.add("Can't declare global varible at " + token);
        }

        VisibilityZone zone = getVisibleParent();

        String name = token.varToken.name.toTokenString();
        Variable var = localVariables.get(name);

        if (var != null) {
            errors.add("Duplicate local variable " + token);
            return var;
        }

        var = zone.createVariable(token.typeToken.type);
        localVariables.put(name, var);
        zone.decVars.add(name);
        return var;
    }

    public Variable createVariable(Type type) {
        if (type.idVoid()) {
            throw new RuntimeException("Can't declare void variable");
        }
        Variable variable = new Variable(type, this, vars.size());
        vars.add(variable);
        return variable;
    }

    public VisibilityZone getVisibleParent() {
        VisibilityZone cur = this;
        while (!cur.visible) {
            cur = cur.parent;
        }
        return cur;
    }

    @Override
    public void println(PrintWriter out, int indent) {
        label();
        printLabel(out, indent);

        boolean sep = false;

        out.print("(");
        for (String var : decVars) {
            if (sep) {
                out.print(", ");
            }
            out.print(var);
            sep = true;
        }

        if (visible) {
            out.println(") { // " + token);
        } else {
            out.println(") ( // " + token);
        }

        for (Action action : actions) {
            action.println(out, indent + 1);
        }

        printIndent(out, indent);
        if (visible) {
            out.println("}");
        } else {
            out.println(")");
        }
    }

    public void removeAll(Map<String, Variable> localVariables) {
        for (String var : decVars) {
            localVariables.remove(var);
        }
    }

    public FunctionZone root() {
        return root;
    }

    public VisibilityZone subZone(boolean visible, Token token) {
        VisibilityZone zone = new VisibilityZone(this, visible, token);
        actions.add(zone);
        return zone;
    }

}
