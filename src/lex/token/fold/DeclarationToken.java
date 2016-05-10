package lex.token.fold;

import lex.Token;
import lex.token.pure.SimpleString;
import exception.Log;
import exception.ParseException;

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

    public DeclarationToken addPac(SimpleString pac, Log log) throws ParseException {
        return new DeclarationToken(typeToken, varToken.addPac(pac, log));
    }

    public DeclarationToken removePac(Log log) throws ParseException {
        return new DeclarationToken(typeToken, varToken.removePac(log));
    }

}
