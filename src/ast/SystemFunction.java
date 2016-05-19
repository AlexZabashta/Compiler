package ast;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import asm.Command;
import code.AsmFunctionZone;
import code.Environment;
import code.FunctionZone;
import exception.Log;
import exception.ParseException;
import lex.token.fold.DeclarationToken;
import lex.token.fold.TypeToken;
import lex.token.fold.VarToken;
import lex.token.pure.SimpleString;
import misc.Characters;
import misc.Type;

public class SystemFunction extends Function {
    public final static String PAC = "sys";
    private final String sysName;
    private final AsmFunctionZone zone;

    public static DeclarationToken declaration(Type type, String name) {
        TypeToken typeToken = new TypeToken(type, null);
        VarToken varToken = new VarToken(new SimpleString(PAC, null), new SimpleString(name, null));
        return new DeclarationToken(typeToken, varToken);
    }

    public SystemFunction(List<Command> asmCode, Type type, String name, Type... args) {
        super(declaration(type, name), new ArrayList<DeclarationToken>(), null);

        StringBuilder builder = new StringBuilder();

        builder.append("sys." + name);

        for (Type t : args) {
            builder.append(Characters.typeSeparator);
            builder.append(t);
        }
        this.sysName = builder.toString();
        this.zone = new AsmFunctionZone(asmCode, this);

    }

    @Override
    public FunctionZone getVisibilityZone(Environment environment, Log log) throws ParseException {
        return zone;
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
