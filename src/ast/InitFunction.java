package ast;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lex.token.fold.DeclarationToken;
import ast.node.op.FBracketsNode;
import code.Environment;
import code.FunctionZone;
import code.var.GlobalVariable;
import code.var.Variable;
import exception.Log;
import exception.ParseException;

public class InitFunction extends Function {

    public InitFunction(DeclarationToken initDeclaration, List<DeclarationToken> vars, FBracketsNode action) {
        super(initDeclaration, vars, action);
    }

    @Override
    public FunctionZone getVisibilityZone(Map<String, GlobalVariable> globalVariables, Map<String, Function> functions, Log log) throws ParseException {
        FunctionZone zone = new FunctionZone(this);
        Map<String, Variable> localVariables = new HashMap<String, Variable>();
        Environment environment = new Environment(globalVariables, functions);
        action.action(zone, environment, log);
        return zone;
    }

    @Override
    public void println(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.print("init");
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
        return name.varToken.toTokenString();
    }

}
