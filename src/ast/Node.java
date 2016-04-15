package ast;

import java.io.PrintWriter;
import java.util.List;

import lex.BadToken;

public class Node {
	public final String type;
	public final List<Node> nodes;
	public final BadToken token;

	public Node(BadToken token) {
		this("leaf", token, null);
	}

	public Node(String type, List<Node> nodes) {
		this(type, null, nodes);
	}

	public Node(String type, BadToken token, List<Node> nodes) {
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
			for (Node node : nodes) {
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
