package code;

import java.io.PrintWriter;

import lex.Token;

public abstract class Action {

    private String label;
    public final Token token;
    private static int nextLabel = 0;

    public String label() {
        if (label == null) {
            label = "$label" + (++nextLabel);
        }
        return label;
    }

    public Action(Token token) {
        this.token = token;
    }

    public Action(String label, Token token) {
        this.label = label;
        this.token = token;
    }

    public abstract void println(PrintWriter out, int indent);

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

}
