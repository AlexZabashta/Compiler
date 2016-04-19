package ast.node;

import java.io.PrintWriter;

import ast.Node;

public class Nope extends Node {

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println("NOP");
	}

	@Override
	public String toString() {
		return "nop";
	}

}
