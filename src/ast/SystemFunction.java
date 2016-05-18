package ast;

import java.io.PrintWriter;
import java.util.ArrayList;

import lex.token.fold.DeclarationToken;
import lex.token.fold.TypeToken;
import lex.token.fold.VarToken;
import lex.token.pure.SimpleString;
import misc.Characters;
import misc.Type;

public class SystemFunction extends Function {

    private final String sysName;

    public static DeclarationToken declaration(Type type, String name) {
        TypeToken typeToken = new TypeToken(type, null);
        VarToken varToken = new VarToken(new SimpleString("sys", null), new SimpleString(name, null));
        return new DeclarationToken(typeToken, varToken);
    }

    public SystemFunction(Type type, String name, String args) {
        super(declaration(type, name), new ArrayList<DeclarationToken>(), null);

        StringBuilder builder = new StringBuilder();

        builder.append("sys." + name + args);

        this.sysName = builder.toString();

    }

    public SystemFunction(Type type, String name, Type... args) {
        super(declaration(type, name), new ArrayList<DeclarationToken>(), null);

        StringBuilder builder = new StringBuilder();

        builder.append("sys." + name);

        for (Type t : args) {
            builder.append(Characters.typeSeparator);
            builder.append(t);
        }
        this.sysName = builder.toString();

    }

    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.print(name.toTokenString());
        out.print('(');
        boolean sep = false;
        for (DeclarationToken var : vars) {
            if (sep) {
                out.print(", ");
            }
            out.print(var.toTokenString());
            sep = true;
        }
        out.println(')');
        out.println("!system");
    }

    @Override
    public String toString() {
        return sysName;
    }
}
