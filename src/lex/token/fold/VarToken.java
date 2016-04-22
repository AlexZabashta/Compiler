package lex.token.fold;

import java.util.List;

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
            return pac.toTokenString() + "." + name;
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

    public VarToken addPac(String pac, List<String> errors) {
        if (this.pac != null) {
            if (this.pac.string.equals(pac)) {
                return this;
            }
            errors.add("Unexpected pac name \"" + this.pac.string + "\" in \"" + pac + "\" pac at " + location);
        }
        return new VarToken(new SimpleString(pac, location), name);
    }

    public VarToken removePac(List<String> errors) {
        if (pac == null) {
            return this;
        } else {
            errors.add("Can't declare global varible at " + location);
            return new VarToken(null, name);
        }
    }
}
