package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lex.Token;
import misc.KeyWords;
import misc.Operators;

public class OperatorTree {
	public static Node build(Node vertex, List<String> errors) {
		if (vertex.nodes == null) {
			return vertex;
		}

		List<Node> nodes = vertex.nodes;

		if (vertex.type == "return") {
			List<Node> retNodes = new ArrayList<Node>();
			retNodes.add(build(new Node("()", null, nodes), errors));
			return new Node(vertex.type, vertex.token, retNodes);
		}

		int size = nodes.size();

		int opId = -1;

		for (int p = 0; opId == -1 && p < 10; p++) {
			for (int id, i = 0; opId == -1 && i < size; i++) {
				if (p == 0) {
					id = i;
				} else {
					id = size - i - 1;
				}

				Token token = nodes.get(id).token;
				if (token != null && Operators.getPriority(token.type) == p) {
					opId = id;
				}
			}
		}

		List<Node> opNodes = new ArrayList<Node>();
		if (opId == -1 || vertex.type == "root" || KeyWords.contains(vertex.type)) {
			for (Node node : nodes) {
				opNodes.add(build(node, errors));
			}
			return new Node(vertex.type, vertex.token, opNodes);
		}

		Token opToken = nodes.get(opId).token;

		for (Node node : nodes) {
			if (node.type == "root" || KeyWords.contains(node.type)) {
				if (node.token == null) {
					errors.add("Cant aply operator " + opToken + " whith " + node.type);
				} else {
					errors.add("Cant aply operator " + opToken + " whith " + node.token);
				}
				return vertex;
			}
		}

		opNodes.add(build(new Node("()", null, nodes.subList(0, opId)), errors));
		opNodes.add(build(new Node("()", null, nodes.subList(opId + 1, size)), errors));

		return new Node(opToken.type, opToken, opNodes);
	}
}
