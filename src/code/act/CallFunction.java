package code.act;

import java.io.PrintWriter;
import java.util.List;

import misc.Type;
import asm.Command;
import asm.com.Mov;
import asm.com.Pop;
import asm.com.ShiftEsp;
import asm.com.Push;
import asm.mem.CpuRegister;
import asm.mem.RWMemory;
import ast.Function;
import code.Action;
import code.var.LocalVariable;
import exception.TypeMismatch;

public class CallFunction extends Action {

    public final List<LocalVariable> args;
    public final Function function;
    public final LocalVariable res;

    public CallFunction(LocalVariable res, Function function, List<LocalVariable> args, String label, String comment) throws TypeMismatch {
        super(label, comment);
        this.function = function;
        this.args = args;
        this.res = res;

        for (LocalVariable variable : args) {
            variable.use(1);
        }

        if (res != null) {
            if (res.type.dim == 0) {
                res.use(1);
            } else {
                res.use(2);
            }
        }

        Type type = res == null ? (new Type()) : res.type;

        if (!function.type.equals(type)) {
            throw new TypeMismatch(type, function.type);
        }
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());

        for (LocalVariable var : args) {
            programText.add(new Push(var.memory(), null, null));
            parent.shiftStack(1); // PUSH
        }

        programText.add(new asm.com.Call(function.toString(), null, null));

        parent.shiftStack(-args.size()); // POP
        programText.add(new ShiftEsp(args.size(), null, null));

        if (res != null) {
            RWMemory eax = new CpuRegister();
            if (res.type.dim != 0) {
                programText.add(new Push(eax, null, null));
                parent.shiftStack(1); // PUSH
                LocalVariable.unsubscribe(programText, res.type, res.rwMemory());
                parent.shiftStack(-1); // POP
                programText.add(new Pop(eax, null, null));
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
