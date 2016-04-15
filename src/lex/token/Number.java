package lex.token;

import java.io.PrintWriter;

import lex.Location;

public class Number extends Token {

	public final int number;

	public Number(String string, Location location) {
		super(location);
		this.number = Integer.parseInt(string);
	}

	@Override
	public void printToken(PrintWriter out) {
		out.print(number);
	}

}
