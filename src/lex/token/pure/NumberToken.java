package lex.token.pure;

import lex.Location;
import lex.Token;
import lex.token.ConstValue;
import misc.LittleEndian;

public class NumberToken extends Token implements ConstValue {

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
    public int getValIndex() {
        return valIndex;
    }

    @Override
    public void setValIndex(int valIndex) {
        this.valIndex = valIndex;
    }

    @Override
    public byte[] getConstValue() {
        return LittleEndian.encode(number);
    }
}
