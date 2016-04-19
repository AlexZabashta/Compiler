package ast.node;

import java.io.PrintWriter;

import ast.Node;
import lex.token.fold.BracketsToken;
import lex.token.key_word.IfToken;

public class IfNode extends Node {

	public final IfToken ifToken;
	public final RBracketsNode state;
	public final FBracketsNode x, y;

	public IfNode(IfToken ifToken, RBracketsNode state, FBracketsNode x, FBracketsNode y) {
		this.ifToken = ifToken;
		this.state = state;
		this.x = x;
		this.y = y;
	}

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println("if");
		state.printTree(out, indent);
		x.printTree(out, indent + 1);
		printIndent(out, indent);
		out.println("else");
		y.printTree(out, indent + 1);
	}

}
