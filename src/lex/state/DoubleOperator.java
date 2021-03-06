package lex.state;

import java.util.List;

import exception.SyntaxesException;
import lex.Location;
import lex.Token;
import lex.TokenBuilder;

public class DoubleOperator extends State {

    public final String first;
    public final char[] follow;

    public DoubleOperator(char first, char... follow) {
        this.first = Character.toString(first);
        this.follow = follow.clone();
    }

    @Override
    public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) throws SyntaxesException {
        for (char c : follow) {
            if (symbol == c) {
                output.add(new lex.token.pure.Operator(first + symbol, builder.location));
                return START;
            }
        }
        output.add(new lex.token.pure.Operator(first, builder.location));
        return START.nextState(symbol, output, builder, location);
    }
}
