package lex.token;

import java.io.PrintWriter;

import lex.Location;
import misc.Characters;

public class Comment extends Token {

	public final String string;

	public Comment(String string, Location location) {
		super(location);
		this.string = string.intern();
	}

	@Override
	public void printToken(PrintWriter out) {
		out.print('!');
		out.print(string);
	}
}
