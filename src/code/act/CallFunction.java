package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import asm.com.Push;
import code.Action;
import code.Variable;

public class CallFunction extends Action {

    public final String fun;
    public final List<Variable> args;
    public final Variable res;

    public CallFunction(Variable res, String fun, List<Variable> args, String label, String comment) {
        super(label, comment);
        this.fun = fun;
        this.args = args;
        this.res = res;
    }

    @Override
    public String toString() {
        return fun + "()";
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);

        if (res == null) {
            out.println(fun + "(" + args + ")");
        } else {
            out.println(res + " = " + fun + "(" + args + ")");
        }
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(start());

        for (Variable var : args) {
            var.subscribe(programText);
            programText.add(new Push(var.memory(), null, comment));
            parent.push();
        }

        programText.add(new asm.com.Call(fun, null, comment));

        for (Variable var : args) {
            parent.pop();
        }

    }

}
