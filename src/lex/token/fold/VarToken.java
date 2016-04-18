package lex.token.fold;

import lex.Token;
import lex.token.pure.SimpleString;

public class VarToken extends Token {

    public final SimpleString pac, name;

    public VarToken(SimpleString pac, SimpleString name) {
        super(name.location);
        this.pac = pac;
        this.name = name;
    }

    @Override
    public String toString() {
        if (pac == null) {
            return name.toString();
        } else {
            return pac + "." + name;
        }

    }

    @Override
    public String toTokenString() {
        if (pac == null) {
            return name.toTokenString();
        } else {
            return pac.toTokenString() + "." + name.toTokenString();
        }
    }

}
