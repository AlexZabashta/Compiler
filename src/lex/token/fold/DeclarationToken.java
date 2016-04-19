package lex.token.fold;

import java.util.List;

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

    public DeclarationToken addPac(String pac, List<String> errors) {
        return new DeclarationToken(typeToken, varToken.addPac(pac, errors));
    }

    public DeclarationToken removePac(List<String> errors) {
        return new DeclarationToken(typeToken, varToken.removePac(errors));
    }

}
