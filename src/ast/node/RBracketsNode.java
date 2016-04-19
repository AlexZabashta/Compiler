package ast.node;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ast.Node;
import lex.Location;
import lex.Token;
import lex.token.fold.BracketsToken;

public class RBracketsNode extends Node {

	public final Token token;
	public final Node node;

	public RBracketsNode(Node node, Token token) {
		this.node = node;
		this.token = token;
	}

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println(token);
		node.printTree(out, indent + 1);
	}

	@Override
	public String toString() {
		return token.toString();
	}

}
