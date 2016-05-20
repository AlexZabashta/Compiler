package lex.token.key_word;

import code.var.ConstVariable;
import lex.Location;
import lex.Token;
import lex.token.ConstValueToken;
import misc.EnumType;
import misc.LittleEndian;
import misc.Type;

public class BoolToken extends ConstValueToken {

    public final boolean value;

    public BoolToken(boolean value, Location location) {
        super(location);
        this.value = value;
    }

    @Override
    public String toTokenString() {
        return Boolean.toString(value);
    }

    @Override
    public Type type() {
        return new Type(EnumType.BOOL);
    }

    @Override
    public String stringValue() {
        return null;
    }

    @Override
    public int intValue() {
        if (value) {
            return ConstVariable.TRUE.value;
        } else {
            return ConstVariable.FALSE.value;
        }
    }

}
