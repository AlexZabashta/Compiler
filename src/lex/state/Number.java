package lex.state;

import java.util.List;

import lex.Location;
import lex.Token;
import lex.TokenBuilder;
import misc.Characters;

public class Number extends State {

	@Override
	public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) {
		if (Characters.isDigit(symbol)) {
			builder.text.append(symbol);
			return this;
		}

		output.add(new lex.token.pure.Number(Integer.parseInt(builder.text.toString()), builder.location));
		builder.text.setLength(0);
		return START.nextState(symbol, output, builder, location);
	}
}
