package lex.token;

import lex.Location;
import lex.Token;
import misc.Type;
import code.var.ConstVariable;

public abstract class ConstValueToken extends Token {

    public ConstVariable variable;

    public ConstValueToken(Location location) {
        super(location);
    }

    public abstract Type type();

}
