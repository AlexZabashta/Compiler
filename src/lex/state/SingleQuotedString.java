package lex.state;

import java.util.List;

import lex.Location;
import lex.token.Token;
import lex.TokenBuilder;
import misc.Characters;

public class SingleQuotedString extends State {

	@Override
	public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) {
		if (Characters.isEndOfLine(symbol)) {
			throw new IllegalStateException("Unexpected new line at " + location);
		}

		if (symbol == '\'') {
			String text = builder.text.toString();

			if (text.length() == 1) {
				output.add(new lex.token.SingleCharString(text.charAt(0), builder.location));
				builder.text.setLength(0);
				return START;
			} else {
				output.add(new lex.token.SingleCharString(text.charAt(0), builder.location));
				if (text.isEmpty()) {
					throw new IllegalStateException("Single quoted string is empty at " + location);
				} else {
					throw new IllegalStateException("Single quoted string must contain only one character at " + location);
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
