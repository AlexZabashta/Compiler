package code.act;

import java.io.PrintWriter;
import java.util.List;

import asm.Command;
import lex.Token;
import code.Action;
import code.Variable;

public class LoadGVar extends Action {
    public final String src;
    public final Variable dst;

    public LoadGVar(Variable dst, String src, Token token) {
        super(token);
        this.dst = dst;
        this.src = src;

    }

    @Override
    public void println(PrintWriter out, int indent) {
        printLabel(out, indent);
        out.println(dst + " = " + src);
    }

    @Override
    public void asm(List<Command> programText) {
        programText.add(label() + ":" + comment());
        programText.add("        mov eax, [" + src + "]");
        programText.add("        mov [esp + " + (dst.distance(parent) * 4) + "], eax");
    }

}
