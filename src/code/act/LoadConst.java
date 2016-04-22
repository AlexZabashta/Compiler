package code.act;

import java.io.PrintWriter;
import java.util.List;

import lex.Token;
import code.Action;
import code.Variable;

public class LoadConst extends Action {
    public final Variable dst;
    public final String src;

    public LoadConst(Variable dst, int valIndex, String label, Token token) {
        super(label, token);
        this.dst = dst;
        this.src = "val" + valIndex;

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(dst + " = load const(" + src + ")");
    }

    @Override
    public void asm(List<String> programText, List<String> errors) {
        programText.add(label() + ":" + comment());

        if (dst.type.level == 0) {
            programText.add("        mov eax, [" + src + "]");
        } else {
            programText.add("        push dword " + src);
            programText.add("        call sys.copy$int.1");
            programText.add("        pop eax");
        }

        programText.add("        mov [esp + " + (dst.distance(parent) * 4) + "], eax");
    }
}
