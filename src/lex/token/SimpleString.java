package lex.token;

import java.io.PrintWriter;

import lex.Location;

public class SimpleString extends Token {

	public final String string;

	public SimpleString(String string, Location location) {
		super(location);
		this.string = string.toLowerCase().intern();
	}

	@Override
	public void printToken(PrintWriter out) {
		out.print(string);
	}

}
