package ast;

import java.util.ArrayList;
import java.util.List;

import lex.Token;

public class CommaTree {
	public static Node build(Node vertex, List<String> errors) {

		if (vertex.nodes == null) {
			return vertex;
		} else {
			List<Node> seq = new ArrayList<Node>();

			Token sep = null;
			Node subSeq = new Node("seq", new ArrayList<Node>());

			for (Node node : vertex.nodes) {

				if (node.token != null) {
					if (node.token.type == ";") {
						if (sep == null) {
							sep = node.token;
						}
						if (sep.type != ";") {
							errors.add("Expected " + sep.type + " separator at " + node.token);
						}
						seq.add(subSeq);
						subSeq = new Node("seq", new ArrayList<Node>());
						continue;
					} else if (node.token.type == ",") {
						if (sep == null) {
							sep = node.token;
						}
						if (sep.type != ",") {
							errors.add("Expected " + sep.type + " separator at " + node.token);
						}
						seq.add(subSeq);
						subSeq = new Node("seq", new ArrayList<Node>());
						continue;
					}
				}

				subSeq.nodes.add(build(node, errors));
			}
			seq.add(subSeq);

			if (vertex.type == "root") {
				if (sep != null && sep.type == ",") {
					errors.add("Unexpected separator in root node at " + sep);
				}
				return new Node(vertex.type, vertex.token, seq);
			}

			if (vertex.type == "()") {
				if (sep == null) {
					return new Node(vertex.type, vertex.token, seq);
				} else {
					return new Node("(" + sep.type + ")", vertex.token, seq);
				}
			}

			if (vertex.type == "{}") {
				if (sep == null) {
					return new Node("{;}", vertex.token, seq);
				} else {
					if (sep.type == ",") {
						errors.add("Unexpected separator in {} node at " + sep);
					}
					return new Node("{;}", vertex.token, seq);
				}
			}

			if (vertex.type == "[]") {
				if (sep == null) {
					return new Node("[;]", vertex.token, seq);
				} else {
					if (sep.type == ",") {
						errors.add("Unexpected separator in [] node at " + sep);
					}
					return new Node("[;]", vertex.token, seq);
				}
			}

			return new Node(vertex.type, vertex.token, seq);
		}
	}
}
