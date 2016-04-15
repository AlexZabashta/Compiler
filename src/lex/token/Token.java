package lex.token;

import java.io.PrintWriter;

import lex.Location;
import tmpast.Node;

public abstract class Token extends Node {

	public final Location location;

	public Token(Location location) {
		super();
		this.location = location;
	}

	@Override
	public void print(PrintWriter out, int indent) {
		printIndent(out, indent);
		printToken(out);
		out.print(' ');
		out.println(location);
	}

	public abstract void printToken(PrintWriter out);

}
