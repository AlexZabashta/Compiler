package ast;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import misc.Characters;
import misc.Type;
import ast.node.op.FBracketsNode;
import code.Environment;
import code.FunctionZone;
import code.var.GlobalVariable;
import code.var.LocalVariable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.UnexpectedVoidType;

public class Function {
    public final FBracketsNode action;
    public final DeclarationToken name;
    private final String string;
    public final Type type;
    public final List<DeclarationToken> vars;

    public Function(DeclarationToken name, List<DeclarationToken> vars, FBracketsNode action) {
        this.type = name.typeToken.type;
        this.name = name;
        this.vars = vars;
        this.action = action;

        StringBuilder builder = new StringBuilder();

        builder.append(name.varToken.toTokenString());

        for (DeclarationToken token : vars) {
            builder.append(Characters.typeSeparator);
            builder.append(token.typeToken.toTokenString());
        }

        this.string = builder.toString();

    }

    public FunctionZone getVisibilityZone(Environment environment, Log log) throws ParseException {
        FunctionZone zone = new FunctionZone(this);

        for (DeclarationToken var : vars) {
            try {
                LocalVariable variable = zone.createVariable(var, environment);
                zone.args.add(variable);
            } catch (UnexpectedVoidType | DeclarationException exceptione) {
                log.addException(new SemanticException(exceptione, name));
            }
        }

        if (!type.idVoid()) {
            try {
                DeclarationToken f = new DeclarationToken(name.typeToken, new VarToken(null, name.varToken.name));
                LocalVariable variable = zone.createVariable(f, environment);
                zone.result = variable;
            } catch (UnexpectedVoidType | DeclarationException exceptione) {
                log.addException(new SemanticException(exceptione, name));
            }
        }
        zone.vars.clear();

        action.action(zone, environment, log);
        environment.removeAllLocalVariables();
        return zone;
    }

    public void printIndent(PrintWriter out, int indent) {
        while (--indent >= 0) {
            out.print("    ");
        }
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
        action.println(out, indent + 1);
        out.println();
    }

    @Override
    public String toString() {
        return string;
    }
}
