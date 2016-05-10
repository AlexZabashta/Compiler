package lex.token.fold;

import lex.Token;
import lex.token.pure.SimpleString;
import exception.Log;
import exception.ParseException;
import exception.SyntaxesException;

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

    public VarToken addPac(SimpleString pac, Log log) throws ParseException {
        if (this.pac != null) {
            if (this.pac.string.equals(pac.string)) {
                return this;
            }
            log.addException(new SyntaxesException("Can't declare outer global varible here", this.pac));
        }
        return new VarToken(pac, name);
    }

    public VarToken removePac(Log log) throws ParseException {
        if (pac == null) {
            return this;
        } else {
            log.addException(new SyntaxesException("Can't declare global varible here", this.pac));
            return new VarToken(null, name);
        }
    }
}
