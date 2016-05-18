package lex.token.pure;

import lex.Location;
import lex.Token;
import lex.token.ConstValueToken;
import misc.Characters;
import misc.EnumType;
import misc.LittleEndian;
import misc.Type;

public class CharToken extends ConstValueToken {

    public final char symbol;
    public int valIndex;

    public CharToken(char symbol, Location location) {
        super(location);
        this.symbol = symbol;
    }

    @Override
    public String toTokenString() {
        return "\'" + Characters.escape(symbol) + "\'";
    }

    @Override
    public Type type() {
        return new Type(EnumType.CHAR);
    }
}
