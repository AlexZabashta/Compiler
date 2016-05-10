package lex.state;

import java.util.List;

import exception.SyntaxesException;
import lex.Location;
import lex.Token;
import lex.TokenBuilder;
import misc.Characters;

public class SingleQuotedString extends State {

    @Override
    public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) throws SyntaxesException {
        if (Characters.isEndOfLine(symbol)) {
            throw new SyntaxesException("Unexpected character", "new line", location);
        }

        if (symbol == '\'') {
            String text = builder.text.toString();

            if (text.length() == 1) {
                output.add(new lex.token.pure.CharToken(text.charAt(0), builder.location));
                builder.text.setLength(0);
                return START;
            } else {
                output.add(new lex.token.pure.CharToken(text.charAt(0), builder.location));
                if (text.isEmpty()) {
                    throw new SyntaxesException("Single quoted string is empty", symbol, location);
                } else {
                    throw new SyntaxesException("Single quoted string must contain only one character", symbol, location);
                }
            }
        }

        if (symbol == '\\') {
            return ESC_IN_SQ_STRING;
        }

        builder.text.append(symbol);
        return this;
    }
}
