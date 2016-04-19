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

public class CallNode extends Node {

	public final VarToken fun;
	public final List<Node> vars;

	public CallNode(VarToken fun, List<Node> vars) {
		this.fun = fun;
		this.vars = vars;
	}

	@Override
	public void printTree(PrintWriter out, int indent) {
		printIndent(out, indent);
		out.println(fun);
		for (Node node : vars) {
			node.printTree(out, indent + 1);
		}
	}

	@Override
	public String toString() {
		return "call " + fun.toString();
	}

}
