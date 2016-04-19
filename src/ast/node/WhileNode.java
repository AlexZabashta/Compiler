package ast.node;

import java.io.PrintWriter;

import ast.Node;
import lex.token.key_word.Logic;
import lex.token.key_word.WhileToken;

public class WhileNode extends Node {

	public final WhileToken whileToken;
	public final Node state, action;

	public WhileNode(WhileToken whileToken, Node state, Node action) {
		this.whileToken = whileToken;
		this.state = state;
		this.action = action;
	}

	public WhileNode(WhileToken whileToken, Node action) {
		this(whileToken, new Leaf(new Logic(false, whileToken.location)), action);
	}

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println(whileToken);
		state.printTree(out, indent + 1);
		action.printTree(out, indent + 2);
	}

	@Override
	public String toString() {
		return whileToken.toString();
	}

}
