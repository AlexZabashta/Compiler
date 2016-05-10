package code;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import asm.Command;
import lex.Token;
import lex.token.fold.DeclarationToken;
import misc.Type;
import code.act.Nop;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.SyntaxesException;

public class VisibilityZone extends Action {

    protected final List<Action> actions = new ArrayList<Action>();
    private final List<String> decVars = new ArrayList<String>();
    public final int level;

    private Nop end = null;

    public Nop end() {
        if (end == null) {
            Nop nop = new Nop();
            addAction(nop);
            end = nop;
        }
        return end;
    }

    public int numberOfVars() {
        return vars.size();
    }

    protected FunctionZone root;

    private final List<Variable> vars = new ArrayList<Variable>();
    public final boolean visible;

    public VisibilityZone(String label, Token token) {
        super(label, token);
        this.level = 0;
        this.parent = null;
        this.visible = true;
    }

    public VisibilityZone parent() {
        return parent;
    }

    public FunctionZone root() {
        return root;
    }

    public VisibilityZone(VisibilityZone parent, boolean visible, Token token) {
        super(token);
        this.parent = parent;
        this.level = parent.level + 1;
        this.visible = visible;
        this.root = parent.root;
    }

    public void addAction(Action action) {
        if (end != null) {
            throw new RuntimeException("Visibility zone " + label() + " is already end");
        }

        actions.add(action);
        if (action.parent == null) {
            action.parent = this;
        }

        if (action.parent != this) {
            throw new RuntimeException("Can't add outer action " + action + " to " + this);
        }

    }

    public Variable createVariable(DeclarationToken token, Map<String, Variable> localVariables, Log log) throws ParseException {
        if (token.varToken.pac != null) {
            log.addException(new SemanticException("Can't declare global varible here", token));
        }

        VisibilityZone zone = getVisibleParent();

        String name = token.varToken.name.toTokenString();
        Variable var = localVariables.get(name);

        if (var != null) {
            log.addException(new SemanticException("Duplicate local variable", token));
            return var;
        }

        var = zone.createVariable(token.typeToken.type);
        localVariables.put(name, var);
        zone.decVars.add(name);
        return var;
    }

    @Override
    public void asm(List<Command> programText) {
        end();
        programText.add(label() + ":" + comment());

        for (Variable variable : vars) {
            if (variable.type.level == 0) {
                programText.add("        push dword 0");
            } else {
                programText.add("        push dword emptyarray");
            }
        }

        for (Action action : actions) {
            action.asm(programText);
        }

        programText.add("        add esp, " + (numberOfVars() * 4));
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

    public VisibilityZone subZone(boolean visible, Token token) {
        VisibilityZone zone = new VisibilityZone(this, visible, token);
        addAction(zone);
        return zone;
    }

}
