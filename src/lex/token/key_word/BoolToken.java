package lex.token.key_word;

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

}
