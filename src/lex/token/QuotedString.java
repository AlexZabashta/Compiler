package lex.token;

import java.io.PrintWriter;

import lex.Location;
import misc.Characters;

public class QuotedString extends Token {

	public final String string;

	public QuotedString(String string, Location location) {
		super(location);
		this.string = string.intern();
	}

	@Override
	public void printToken(PrintWriter out) {
		out.print('"');
		out.print(Characters.escape(string));
		out.print('"');
	}
}
