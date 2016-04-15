package ast;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import lex.Location;
import lex.BadToken;

public class BadNode {
	public final String type;
	public final List<BadNode> nodes;
	public final BadToken token;

	public Location location() {
		if (token == null) {
			return null;
		} else {
			return token.location;
		}
	}

	public String header() {
		if (token == null) {
			return type;
		} else {
			return token.toString();
		}
	}

	public BadNode(BadToken token) {
		this("leaf", token, null);
	}

	public BadNode(String type, List<BadNode> nodes) {
		this(type, null, nodes);
	}

	public BadNode(String type, BadToken token, List<BadNode> nodes) {
		this.type = type.intern();
		this.nodes = nodes;
		this.token = token;
	}

	public void print(PrintWriter out) {
		print(0, out);
	}

	public void print(int tab, PrintWriter out) {
		for (int t = 0; t < tab; t++) {
			out.print('\t');
		}

		out.print(type);
		if (token != null) {
			out.print(' ');
			out.print(token);
		}
		out.println();

		if (nodes != null) {
			for (BadNode node : nodes) {
				node.print(tab + 1, out);
			}
			out.println();
		}
	}

	@Override
	public String toString() {
		String string = type;
		if (nodes != null) {
			string += ' ' + nodes.toString();
		}
		if (token != null) {
			string += ' ' + token.toString();
		}
		return string;
	}
}
