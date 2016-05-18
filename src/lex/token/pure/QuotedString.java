package lex.token.pure;

import lex.Location;
import lex.token.ConstValueToken;
import misc.Characters;
import misc.Type;

public class QuotedString extends ConstValueToken {

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
    public Type type() {
        return Type.string();
    }

    // @Override
    // public byte[] getConstValue() {
    // int len = string.length();
    //
    // ByteBuffer buffer = ByteBuffer.allocate((len + 2) * 4);
    //
    // buffer.put(LittleEndian.encode(-1));
    // buffer.put(LittleEndian.encode(len));
    //
    // for (int i = 0; i < len; i++) {
    // buffer.put(LittleEndian.encode(string.charAt(i)));
    // }
    //
    // return buffer.array();
    // }
}
