package lex.token.fold;

import lex.Location;
import lex.Token;
import misc.Type;

public class TypeToken extends Token {

    public final Type type;

    public TypeToken(Type type, Location location) {
        super(location);
        this.type = type;
    }

    @Override
    public String toTokenString() {
        return type.toString();
    }

}
