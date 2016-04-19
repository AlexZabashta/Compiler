package ast.node;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ast.Node;
import lex.Location;
import lex.Token;
import lex.token.fold.BracketsToken;
import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import lex.token.key_word.ForToken;
import lex.token.key_word.Logic;

public class ForNode extends Node {

	public final ForToken forToken;
	public final Node pre, state, post, action;

	public ForNode(ForToken forToken, Node pre, Node state, Node post, Node action) {
		this.forToken = forToken;
		this.pre = pre;
		this.state = state;
		this.post = post;
		this.action = action;
	}

	public ForNode(ForToken forToken, Node state, Node post, Node action) {
		this(forToken, new Nope(), state, post, action);
	}

	public ForNode(ForToken forToken, Node state, Node action) {
		this(forToken, new Nope(), state, new Nope(), action);
	}

	public ForNode(ForToken forToken, Node action) {
		this(forToken, new Nope(), new Leaf(new Logic(false, forToken.location)), new Nope(), action);
	}

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println(forToken);
		pre.printTree(out, indent + 1);
		state.printTree(out, indent + 1);
		post.printTree(out, indent + 1);
		action.printTree(out, indent + 2);
	}

	@Override
	public String toString() {
		return forToken.toString();
	}

}
