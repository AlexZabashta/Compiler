package code.act;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import asm.Command;
import lex.Token;
import code.Action;
import code.Variable;
import code.VisibilityZone;

public class AsmCode extends Action {

    public final List<String> code = new ArrayList<String>();

    public AsmCode() {
        super(null);
    }

    @Override
    public String toString() {
        return "asm";
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println("asm");
    }

    @Override
    public void asm(List<Command> programText) {
        for (String line : code) {
            programText.add(line);
        }

    }

}
