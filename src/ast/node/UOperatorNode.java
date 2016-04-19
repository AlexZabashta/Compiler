package ast.node;

import java.io.PrintWriter;

import ast.Node;
import lex.Token;
import lex.token.pure.Operator;

public class UOperatorNode extends Node {

	public final Node node;
	public final Operator operator;

	public UOperatorNode(Node node, Operator operator) {
		this.node = node;
		this.operator = operator;
	}

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println(operator);
		node.printTree(out, indent + 1);
	}

	@Override
	public String toString() {
		return operator.toString();
	}

}
