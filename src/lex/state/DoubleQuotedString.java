package lex.state;

import java.util.List;

import lex.Location;
import lex.token.Token;
import lex.TokenBuilder;
import misc.Characters;

public class DoubleQuotedString extends State {

	@Override
	public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) {
		if (Characters.isEndOfLine(symbol)) {
			throw new IllegalStateException("Unexpected new line at " + location);
		}

		if (symbol == '"') {
			output.add(new lex.token.QuotedString(builder.text.toString(), builder.location));
			builder.text.setLength(0);
			return START;
		}

		if (symbol == '\\') {
			return ESC_IN_DQ_STRING;
		}

		builder.text.append(symbol);
		return this;
	}
}
