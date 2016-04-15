package lex.token;

import java.io.PrintWriter;

import lex.Location;

public class Number extends Token {

	public final int number;

	public Number(int number, Location location) {
		super(location);
		this.number = number;
	}

	@Override
	public void printToken(PrintWriter out) {
		out.print(number);
	}

}
