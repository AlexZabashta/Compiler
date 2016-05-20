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

    @Override
    public String stringValue() {

        StringBuilder builder = new StringBuilder();

        for (char c : string.toCharArray()) {
            builder.append(", ");

            if (c != '\'' && c != '\"' && c != '\\') {
                if ((' ' <= c && c <= ']') || ('a' <= c && c <= '}')) {
                    builder.append("'");
                    builder.append(c);
                    builder.append("'");
                    continue;
                }
            }
            builder.append((int) c);

        }

        return builder.toString();
    }

    @Override
    public int intValue() {
        return 0;
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
