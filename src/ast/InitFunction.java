package ast;

import java.util.List;

import ast.node.FBracketsNode;
import lex.token.fold.DeclarationToken;

public class InitFunction extends Function {

    public InitFunction(DeclarationToken initDeclaration, List<DeclarationToken> vars, FBracketsNode action) {
        super(initDeclaration, vars, action);
    }

    @Override
    public String toString() {
        return name.varToken.toTokenString();
    }

}
