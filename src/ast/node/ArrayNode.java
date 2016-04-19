package ast.node;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ast.Node;
import lex.Location;
import lex.Token;
import lex.token.fold.BracketsToken;

public class ArrayNode extends Node {

	public final Token token;
	public final Node array, index;

	public ArrayNode(Node array, Node index, Token token) {
		this.array = array;
		this.index = index;
		this.token = token;
	}

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println(token);
		array.printTree(out, indent + 1);
		index.printTree(out, indent + 1);
	}

	@Override
	public String toString() {
		return token.toString();
	}

}
