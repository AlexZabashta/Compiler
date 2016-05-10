package lex.state;

import java.util.List;

import exception.SyntaxesException;
import lex.Location;
import lex.Token;
import lex.TokenBuilder;
import misc.Characters;

public class EscInStrState extends State {

    public final State nextState;

    public EscInStrState(State nextState) {
        this.nextState = nextState;
    }

    @Override
    public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) throws SyntaxesException {
        if (Characters.isEndOfLine(symbol)) {
            throw new SyntaxesException("Unexpected character", "new line", location);
        }

        builder.text.append(Characters.reEscape(symbol));
        return nextState;
    }
}
