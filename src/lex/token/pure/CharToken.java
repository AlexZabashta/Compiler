package lex.token.pure;

import lex.Location;
import lex.Token;
import lex.token.ConstValue;
import misc.Characters;
import misc.LittleEndian;

public class CharToken extends Token implements ConstValue {

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
    public int getValIndex() {
        return valIndex;
    }

    @Override
    public void setValIndex(int valIndex) {
        this.valIndex = valIndex;
    }

    @Override
    public byte[] getConstValue() {
        return LittleEndian.encode(symbol);
    }
}
