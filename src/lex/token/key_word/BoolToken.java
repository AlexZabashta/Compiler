package lex.token.key_word;

import lex.Location;
import lex.Token;
import lex.token.ConstValue;
import misc.LittleEndian;

public class BoolToken extends Token implements ConstValue {

    public final boolean value;
    private int valIndex;

    public BoolToken(boolean value, Location location) {
        super(location);
        this.value = value;
    }

    @Override
    public String toTokenString() {
        return Boolean.toString(value);
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
        if (value) {
            return LittleEndian.encode(-1);
        } else {
            return LittleEndian.encode(0);
        }
    }

}
