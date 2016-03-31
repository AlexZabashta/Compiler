package lex;

import misc.Characters;
import misc.KeyWords;

public class Token {
	public final String type;
	public final String text;
	public final Location location;

	public Token(String type, Location location) {
		this(type, null, location);
	}

	public Token(String type, String text, Location location) {
		this.location = location;
		if (type == "str" && KeyWords.contains(text)) {
			this.type = text.intern();
			this.text = null;
		} else {
			this.type = type.intern();
			this.text = text;
		}
	}

	public String toOriginalString() {
		switch (type) {
		case "str":
		case "num":
			return text;
		case "sqs":
			return '\'' + Characters.escape(text) + '\'';
		case "dqs":
			return '\"' + Characters.escape(text) + '\"';
		case "#":
			return '#' + text;
		default:
			return type;
		}
	}

	public String toString(boolean addLocation) {
		if (addLocation) {
			if (text == null || text.isEmpty()) {
				return type + " [" + location + "]";
			} else {
				return type + " \"" + Characters.escape(text) + "\" [" + location + "]";
			}
		} else {
			if (text == null || text.isEmpty()) {
				return type;
			} else {
				return type + " \"" + Characters.escape(text) + "\"";
			}
		}
	}

	@Override
	public String toString() {
		return toString(true);
	}

}
