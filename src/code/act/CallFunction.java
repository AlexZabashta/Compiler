package code.act;

import java.io.PrintWriter;
import java.util.List;

import misc.Type;
import asm.Command;
import asm.com.Mov;
import asm.com.PopNull;
import asm.com.Push;
import asm.mem.CpuRegister;
import asm.mem.RWMemory;
import ast.Function;
import code.Action;
import code.var.Variable;
import exception.TypeMismatch;

public class CallFunction extends Action {

    public final List<Variable> args;
    public final Function function;
    public final Variable res;

    public CallFunction(Variable res, Function function, List<Variable> args, String label, String comment) throws TypeMismatch {
        super(label, comment);
        this.function = function;
        this.args = args;
        this.res = res;

        Type type = res == null ? (new Type()) : res.type;

        if (!function.type.equals(type)) {
            throw new TypeMismatch(type, function.type);
        }
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());

        for (Variable var : args) {
            programText.add(new Push(var.memory(), null, null));
            parent.push();
        }

        programText.add(new asm.com.Call(function.toString(), null, null));

        parent.pop(args.size());
        programText.add(new PopNull(args.size(), null, null));

        if (res != null) {
            RWMemory eax = new CpuRegister();
            if (res.type.dim != 0) {
                programText.add(new Push(eax, null, null));
                parent.push();
                Variable.unsubscribe(programText, res.type, res.rwMemory());
                parent.pop(1);
            }
            programText.add(new Mov(res.rwMemory(), eax, null, null));
        }
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);

        if (res == null) {
            out.println(function + "(" + args + ")");
        } else {
            out.println(res + " = " + function + "(" + args + ")");
        }
    }

    @Override
    public String toString() {
        return function + "()";
    }

}
