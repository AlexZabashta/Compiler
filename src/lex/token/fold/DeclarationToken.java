package lex.token.fold;

import lex.Token;

public class DeclarationToken extends Token {

    public final TypeToken typeToken;
    public final VarToken varToken;

    public DeclarationToken(TypeToken typeToken, VarToken varToken) {
        super(varToken.location);
        this.typeToken = typeToken;
        this.varToken = varToken;
    }

    @Override
    public String toTokenString() {
        return typeToken.toTokenString() + " " + varToken.toTokenString();
    }

}
