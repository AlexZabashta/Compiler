package asm;

import java.io.PrintWriter;

import lex.Token;

public abstract class AsmLine {

    public String label, comment;

    public AsmLine(String label, String comment) {
        this.label = label;
        this.comment = comment;
    }

    public abstract String toStringYASM_WIN_32();

    public void printYASM_WIN_32(PrintWriter out, int indent) {
        if (label == null) {
            int space = indent * 4;
            while (--space >= 0) {
                out.print(' ');
            }
        } else {
            int space = indent * 4 - label.length() - 2;

            while (--space >= 0) {
                out.print(' ');
            }

            out.print(label);
            out.print(": ");
        }

        out.print(toStringYASM_WIN_32());

        if (comment != null) {
            out.print("    ; ");
            out.print(comment);
        }
        out.println();
    }

    @Override
    public String toString() {
        return toStringYASM_WIN_32();
    }

}
