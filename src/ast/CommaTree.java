package ast;

import java.util.ArrayList;
import java.util.List;

import lex.BadToken;

public class CommaTree {
	public static BadNode build(BadNode vertex, List<String> errors) {

		if (vertex.nodes == null) {
			return vertex;
		} else {
			List<BadNode> seq = new ArrayList<BadNode>();

			BadToken sep = null;
			BadNode subSeq = new BadNode("seq", new ArrayList<BadNode>());

			for (BadNode node : vertex.nodes) {

				if (node.token != null) {
					if (node.token.type == ";") {
						if (sep == null) {
							sep = node.token;
						}
						if (sep.type != ";") {
							errors.add("Expected " + sep.type + " separator at " + node.token);
						}
						seq.add(subSeq);
						subSeq = new BadNode("seq", new ArrayList<BadNode>());
						continue;
					} else if (node.token.type == ",") {
						if (sep == null) {
							sep = node.token;
						}
						if (sep.type != ",") {
							errors.add("Expected " + sep.type + " separator at " + node.token);
						}
						seq.add(subSeq);
						subSeq = new BadNode("seq", new ArrayList<BadNode>());
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
				return new BadNode(vertex.type, vertex.token, seq);
			}

			if (vertex.type == "()") {
				if (sep == null) {
					return new BadNode(vertex.type, vertex.token, seq);
				} else {
					return new BadNode("(" + sep.type + ")", vertex.token, seq);
				}
			}

			if (vertex.type == "{}") {
				if (sep == null) {
					return new BadNode("{;}", vertex.token, seq);
				} else {
					if (sep.type == ",") {
						errors.add("Unexpected separator in {} node at " + sep);
					}
					return new BadNode("{;}", vertex.token, seq);
				}
			}

			if (vertex.type == "[]") {
				if (sep == null) {
					return new BadNode("[;]", vertex.token, seq);
				} else {
					if (sep.type == ",") {
						errors.add("Unexpected separator in [] node at " + sep);
					}
					return new BadNode("[;]", vertex.token, seq);
				}
			}

			return new BadNode(vertex.type, vertex.token, seq);
		}
	}
}
