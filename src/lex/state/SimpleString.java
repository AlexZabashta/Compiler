package lex.state;

import java.util.List;

import lex.Location;
import lex.token.Token;
import lex.TokenBuilder;
import misc.Characters;

public class SimpleString extends State {

	@Override
	public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) {
		if (Characters.isChar(symbol) || Characters.isDigit(symbol) || symbol == '_') {
			builder.text.append(symbol);
			return this;
		}

		output.add(new lex.token.SimpleString(builder.text.toString(), builder.location));
		builder.text.setLength(0);
		return START.nextState(symbol, output, builder, location);
	}
}
