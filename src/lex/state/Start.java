package lex.state;

import java.util.List;

import lex.Location;
import lex.Token;
import lex.TokenBuilder;
import lex.token.pure.Operator;
import misc.Characters;

public class Start extends State {

	@Override
	public State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) {
		if (Characters.isBlank(symbol) || Characters.isEndOfLine(symbol)) {
			return this;
		}

		if (Characters.isChar(symbol)) {
			builder.location = location;
			return SIMPLE_STRING.nextState(symbol, output, builder, location);
		}

		if (Characters.isDigit(symbol)) {
			builder.location = location;
			return NUMBER.nextState(symbol, output, builder, location);
		}

		if (Operator.isOperator(symbol)) {
			switch (symbol) {
			case '<':
				builder.location = location;
				return LESS;
			case '>':
				builder.location = location;
				return GREATER;
			case '=':
				builder.location = location;
				return EQUAL;
			case '&':
				builder.location = location;
				return AND;
			case '|':
				builder.location = location;
				return OR;
			default:
				output.add(new Operator(Character.toString(symbol), location));
				return this;
			}
		}

		switch (symbol) {
		case '!':
			builder.location = location;
			return COMMENT;
		case '"':
			builder.location = location;
			return DQ_STRING;
		case '\'':
			builder.location = location;
			return SQ_STRING;
		default:
			throw new IllegalStateException("Unexpected '" + symbol + "' at " + location);
		}
	}
}
