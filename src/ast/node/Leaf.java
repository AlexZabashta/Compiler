package ast.node;

import java.io.PrintWriter;

import ast.Node;
import lex.Token;

public class Leaf extends Node {
	public final Token token;

	public Leaf(Token token) {
		this.token = token;
	}

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println(token);
	}
}
