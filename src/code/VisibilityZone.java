package code;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import lex.token.fold.DeclarationToken;
import misc.Type;
import asm.Command;
import asm.Register;
import asm.com.Push;
import asm.com.ShiftEsp;
import asm.mem.CpuRegister;
import asm.mem.RamEsp;
import code.act.Nop;
import code.var.LocalVariable;
import code.var.Variable;
import exception.DeclarationException;
import exception.UnexpectedVoidType;
import javafx.print.Collation;

import static asm.Register.*;

public class VisibilityZone extends Action {

    public static final Register[] registers = { EBX, ECX, EDX, EDI, ESI, EBP };
    public static int regPointer = 0;

    protected final List<Action> actions = new ArrayList<Action>();
    private final List<String> decVars = new ArrayList<String>();

    private Nop end = null;
    public final int level;

    protected FunctionZone root;

    protected final List<LocalVariable> vars = new ArrayList<LocalVariable>();

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

    public static Register nextRegister() {
        return registers[(regPointer++) % registers.length];
    }

    @Override
    public void asm(List<Command> programText) {
        Nop end = end();
        programText.add(start());

        int len = vars.size();

        Collections.sort(vars, new Comparator<LocalVariable>() {
            @Override
            public int compare(LocalVariable v, LocalVariable u) {
                return Integer.compare(u.counter, v.counter);
            }
        });

        int reg = Math.min(len, registers.length);

        programText.add(new ShiftEsp(-len, null, null));

        parent.shiftStack(len);

        for (int i = 0; i < reg; i++) {
            LocalVariable nVar = vars.get(i);

            Register register = nextRegister();
            nVar.register = register;

            Stack<LocalVariable> stack = root.reAlloc.get(register);
            int offset = len - i - 1;
            if (!stack.isEmpty()) {
                LocalVariable oVar = stack.peek();
                oVar.offset = offset;
                oVar.register = null;
            }
            stack.push(nVar);

            Variable.moveMem(programText, new RamEsp(offset), new CpuRegister(register));
            Variable.init(programText, nVar.type, nVar.rwMemory());
        }

        for (int i = reg; i < len; i++) {
            LocalVariable variable = vars.get(i);
            variable.offset = len - i - 1;
            variable.register = null;
            Variable.init(programText, variable.type, variable.rwMemory());
        }

        for (Action action : actions) {
            action.asm(programText);
        }

        for (int i = 0; i < reg; i++) {
            LocalVariable nVar = vars.get(i);

            Register register = nVar.register;

            Stack<LocalVariable> stack = root.reAlloc.get(register);
            int offset = len - i - 1;
            stack.pop();

            if (!stack.isEmpty()) {
                LocalVariable oVar = stack.peek();
                oVar.offset = -10000;
                oVar.register = register;
            }

            Variable.unsubscribe(programText, nVar.type, nVar.rwMemory());
            Variable.moveMem(programText, new CpuRegister(register), new RamEsp(offset));
        }

        for (int i = reg; i < len; i++) {
            LocalVariable variable = vars.get(i);
            Variable.unsubscribe(programText, variable.type, variable.rwMemory());
        }

        programText.add(new ShiftEsp(len, null, null));

        parent.shiftStack(-len);

        programText.add(new asm.com.Nop(end.label, end.comment));
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
        if (end != null) {
            throw new RuntimeException("Visibility zone " + label + " is already end");
        }

        if (type.idVoid()) {
            throw new UnexpectedVoidType("Can't declare void variable");
        }
        LocalVariable variable = new LocalVariable(type);
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

    public VisibilityZone getVisibleParent() {
        VisibilityZone cur = this;
        while (!cur.visible) {
            cur = cur.parent;
        }
        return cur;
    }

    public VisibilityZone parent() {
        return parent;
    }

    public void shiftStack(int offset) {
        for (LocalVariable variable : vars) {
            variable.offset += offset;
        }
        parent.shiftStack(offset);
    }

    @Override
    public void printDensely(PrintWriter out, int indent) {
        for (Action action : actions) {
            if (action instanceof Nop) {
                continue;
            }
            action.printDensely(out, indent);
        }
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
