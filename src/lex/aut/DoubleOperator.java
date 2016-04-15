package lex.aut;

import java.util.List;

import lex.Location;
import lex.BadToken;
import lex.TokenBuilder;

public class DoubleOperator extends State {

	public final String first;
	public final char[] follow;

	public DoubleOperator(char first, char... follow) {
		this.first = Character.toString(first);
		this.follow = follow.clone();
	}

	@Override
	public State nextState(char symbol, List<BadToken> output, TokenBuilder builder, Location location) {

		for (char c : follow) {
			if (symbol == c) {
				output.add(new BadToken(first + symbol, builder.location));
				return START;
			}
		}
		output.add(new BadToken(first, builder.location));
		return START.nextState(symbol, output, builder, location);
	}
}
