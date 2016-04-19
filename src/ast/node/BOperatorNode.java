package ast.node;

import java.io.PrintWriter;

import ast.Node;
import lex.Token;
import lex.token.pure.Operator;

public class BOperatorNode extends Node {

	public final Node left, right;
	public final Operator operator;

	public BOperatorNode(Node left, Node right, Operator operator) {
		this.left = left;
		this.right = right;
		this.operator = operator;
	}

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println(operator);
		left.printTree(out, indent + 1);
		right.printTree(out, indent + 1);
	}

	@Override
	public String toString() {
		return operator.toString();
	}

}
