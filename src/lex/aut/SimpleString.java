package lex.aut;

import java.util.List;

import lex.Location;
import lex.Token;
import lex.TokenBuilder;
import misc.Characters;

public class SimpleString extends State {

	@Override
	public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) {
		if (Characters.isChar(symbol) || Characters.isDigit(symbol)) {
			builder.text.append(symbol);
			return this;
		}
		output.add(new Token("str", builder.text.toString().toLowerCase(), builder.location));
		builder.text.setLength(0);
		return START.nextState(symbol, output, builder, location);
	}
}
