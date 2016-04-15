package lex.state;

import java.util.List;

import lex.Location;
import lex.token.Token;
import lex.TokenBuilder;
import misc.Characters;

public class Comment extends State {

	@Override
	public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) {
		if (Characters.isEndOfLine(symbol)) {
			output.add(new lex.token.Comment(builder.text.toString(), builder.location));
			builder.text.setLength(0);
			return State.START;
		}
		builder.text.append(symbol);
		return this;
	}
}
