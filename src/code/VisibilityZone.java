package code;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.token.fold.DeclarationToken;
import misc.Type;
import asm.Command;
import code.act.Nop;
import code.var.LocalVariable;
import exception.DeclarationException;
import exception.UnexpectedVoidType;

public class VisibilityZone extends Action {

    protected final List<Action> actions = new ArrayList<Action>();
    private final List<String> decVars = new ArrayList<String>();
    private Nop end = null;

    public final int level;

    protected FunctionZone root;

    private final List<LocalVariable> vars = new ArrayList<LocalVariable>();

    public final boolean visible;

    public VisibilityZone(String label, String comment) {
        super(label, comment);
        this.level = 0;
        this.parent = null;
        this.visible = true;
    }

    public VisibilityZone(VisibilityZone parent, boolean visible, String comment) {
        super(null, comment);
        this.parent = parent;
        this.level = parent.level + 1;
        this.visible = visible;
        this.root = parent.root;
    }

    public void addAction(Action action) {
        if (end != null) {
            throw new RuntimeException("Visibility zone " + label + " is already end");
        }

        actions.add(action);
        if (action.parent == null) {
            action.parent = this;
        }

        if (action.parent != this) {
            throw new RuntimeException("Can't add outer action " + action + " to " + this);
        }

    }

    @Override
    public void asm(List<Command> programText) {
        end();

        // TODO SAVE REGISTERS

        for (Action action : actions) {
            action.asm(programText);
        }
    }

    public LocalVariable createVariable(DeclarationToken token, Environment environment) throws UnexpectedVoidType, DeclarationException {
        if (token.varToken.pac != null) {
            throw new DeclarationException("Can't declare global varible here");
        }

        VisibilityZone zone = getVisibleParent();

        String name = token.varToken.name.toTokenString();
        LocalVariable var = zone.createVariable(token.typeToken.type);

        environment.addLocalVariable(name, var);

        zone.decVars.add(name);
        return var;
    }

    public LocalVariable createVariable(Type type) throws UnexpectedVoidType {
        if (type.idVoid()) {
            throw new RuntimeException("Can't declare void variable");
        }
        LocalVariable variable = new LocalVariable(type, this, vars.size());
        vars.add(variable);
        return variable;
    }

    public Nop end() {
        if (end == null) {
            Nop nop = new Nop();
            addAction(nop);
            end = nop;
        }
        return end;
    }

    public void freeVars(List<Command> programText) {
        end();

        // TODO SAVE REGISTERS
    }

    public VisibilityZone getVisibleParent() {
        VisibilityZone cur = this;
        while (!cur.visible) {
            cur = cur.parent;
        }
        return cur;
    }

    public int numberOfVars() {
        return vars.size();
    }

    public VisibilityZone parent() {
        return parent;
    }

    public void pop(int offset) {
        // TODO pop vars
    }

    @Override
    public void println(PrintWriter out, int indent) {
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
            out.println(") { // " + comment);
        } else {
            out.println(") ( // " + comment);
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

    public void push() {
        // TODO push vars
    }

    public void removeAll(Environment environment) throws DeclarationException {
        for (String var : decVars) {
            environment.removeLocalVariable(var);
        }
    }

    public FunctionZone root() {
        return root;
    }

    public VisibilityZone subZone(boolean visible, String comment) {
        VisibilityZone zone = new VisibilityZone(this, visible, comment);
        addAction(zone);
        return zone;
    }

}
