package lex.token.pure;

import lex.Location;
import lex.Token;
import lex.token.ConstValueToken;
import misc.EnumType;
import misc.LittleEndian;
import misc.Type;

public class NumberToken extends ConstValueToken {

    public final int number;

    public int valIndex;

    public NumberToken(int number, Location location) {
        super(location);
        this.number = number;
    }

    @Override
    public String toTokenString() {
        return Integer.toString(number);
    }

    @Override
    public Type type() {
        return new Type(EnumType.INT);
    }

}
