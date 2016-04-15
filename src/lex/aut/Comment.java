package lex.aut;

import java.util.List;

import lex.Location;
import lex.BadToken;
import lex.TokenBuilder;
import misc.Characters;

public class Comment extends State {

	@Override
	public State nextState(char symbol, List<BadToken> output, TokenBuilder builder, Location location) {
		if (Characters.isEndOfLine(symbol)) {
			output.add(new BadToken("#", builder.text.toString(), builder.location));
			builder.text.setLength(0);
			return State.START;
		}
		builder.text.append(symbol);
		return this;
	}
}
