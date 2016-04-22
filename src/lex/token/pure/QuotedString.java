package lex.token.pure;

import java.nio.ByteBuffer;

import lex.Location;
import lex.Token;
import lex.token.ConstValue;
import misc.Characters;
import misc.LittleEndian;

public class QuotedString extends Token implements ConstValue {

    public final String string;
    public int valIndex;

    public QuotedString(String string, Location location) {
        super(location);
        this.string = string.intern();
    }

    @Override
    public String toTokenString() {
        return "\"" + Characters.escape(string) + "\"";
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
        int len = string.length();

        ByteBuffer buffer = ByteBuffer.allocate(len * 4);

        for (int i = 0; i < len; i++) {
            buffer.put(LittleEndian.encode(string.charAt(i)));
        }

        return buffer.array();
    }
}
