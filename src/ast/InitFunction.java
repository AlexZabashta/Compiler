package ast;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lex.token.fold.DeclarationToken;
import ast.node.op.FBracketsNode;
import code.Environment;
import code.FunctionZone;
import code.InitFunctionZone;
import code.var.GlobalVariable;
import code.var.Variable;
import exception.Log;
import exception.ParseException;
import exception.UnexpectedVoidType;

public class InitFunction extends Function {

    public final List<GlobalVariable> globalVariables = new ArrayList<GlobalVariable>();

    public InitFunction(DeclarationToken initDeclaration, List<DeclarationToken> vars, FBracketsNode action) throws UnexpectedVoidType {
        super(initDeclaration, new ArrayList<>(), action);

        for (DeclarationToken token : vars) {
            globalVariables.add(new GlobalVariable(token));
        }
    }

    @Override
    public FunctionZone getVisibilityZone(Environment environment, Log log) throws ParseException {
        FunctionZone zone = new InitFunctionZone(globalVariables, this);
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
