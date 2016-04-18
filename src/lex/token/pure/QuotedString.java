package lex.token.pure;

import lex.Location;
import lex.Token;
import misc.Characters;

public class QuotedString extends Token {

    public final String string;

    public QuotedString(String string, Location location) {
        super(location);
        this.string = string.intern();
    }

    @Override
    public String toTokenString() {
        return "\"" + Characters.escape(string) + "\"";
    }
}
