package ast;

import java.util.List;

import lex.token.fold.DeclarationToken;
import misc.Characters;
import misc.Type;

public class Function {
    public final FBracketsNode action;
    public final DeclarationToken name;
    public final String string;
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

    @Override
    public String toString() {
        return string;
    }
}
