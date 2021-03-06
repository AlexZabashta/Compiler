package code;

import java.io.PrintWriter;
import java.util.List;

import misc.Label;
import asm.Command;

public abstract class Action {

    public String label, comment;
    public FunctionZone parent;

    public Action(String label, String comment) {
        if (label == null) {
            this.label = Label.getTextLabel();
        } else {
            this.label = label;
        }
        this.comment = comment;
    }

    public abstract void asm(List<Command> programText);

    public void printLabel(PrintWriter out, int indent) {
        if (label == null) {
            printIndent(out, indent);
        } else {

            int space = indent * 4 - label.length() - 2;

            while (--space >= 0) {
                out.print(' ');
            }

            out.print(label);
            out.print(": ");
        }
    }

    public void printIndent(PrintWriter out, int indent) {
        while (--indent >= 0) {
            out.print("    ");
        }
    }

    public abstract void println(PrintWriter out, int indent);

    public asm.com.Nop start() {
        return new asm.com.Nop(label, comment);
    }
}
