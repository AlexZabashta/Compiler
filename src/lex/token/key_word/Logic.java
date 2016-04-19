package lex.token.key_word;

import lex.Location;
import lex.Token;

public class Logic extends Token {

	public final boolean value;

	public Logic(boolean value, Location location) {
		super(location);
		this.value = value;
	}

	@Override
	public String toTokenString() {
		return Boolean.toString(value);
	}

}
