package lex.aut;

import java.util.List;

import lex.Location;
import lex.Token;
import lex.TokenBuilder;
import misc.Characters;

public class SingleQuotedString extends State {

	@Override
	public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) {
		if (Characters.isEndOfLine(symbol)) {
			throw new IllegalStateException("Unexpected new line at " + location);
		}

		if (symbol == '\'') {
			output.add(new Token("sqs", builder.text.toString(), builder.location));
			builder.text.setLength(0);
			return START;
		}

		if (symbol == '\\') {
			return ESC_IN_SQ_STRING;
		}

		builder.text.append(symbol);
		return this;
	}
}
