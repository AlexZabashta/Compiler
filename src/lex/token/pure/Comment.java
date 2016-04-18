package lex.token.pure;

import lex.Location;
import lex.Token;
import misc.Characters;

public class Comment extends Token {

    public final String string;

    public Comment(String string, Location location) {
        super(location);
        this.string = string.intern();
    }

    @Override
    public String toTokenString() {
        return "!\"" + Characters.escape(string) + "\"";
    }
}
