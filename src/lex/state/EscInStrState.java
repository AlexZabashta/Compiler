package lex.state;

import java.util.List;

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
	public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) {
		if (Characters.isEndOfLine(symbol)) {
			throw new IllegalStateException("Unexpected new line at " + location);
		}

		builder.text.append(Characters.reEscape(symbol));
		return nextState;
	}
}
