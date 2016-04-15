package ast;

import java.util.ArrayList;
import java.util.List;

import lex.Token;
import misc.KeyWords;

public class KeyWordsTree {
	public static BadNode build(BadNode vertex, List<String> errors) {

		if (vertex == null) {
			errors.add("Find null vertex.");
			return new BadNode("seq", null, new ArrayList<BadNode>());
		}

		List<BadNode> nodes = vertex.nodes;

		if (nodes == null) {
			Token token = vertex.token;
			if (token != null) {
				if (KeyWords.contains(token.type)) {
					errors.add("Unexpected key word " + token);
				}
			}
			return vertex;
		}

		List<BadNode> vseq = new ArrayList<BadNode>();

		for (int i = 0; i < nodes.size(); i++) {
			BadNode node = nodes.get(i);

			Token token = node.token;
			if (token == null) {
				vseq.add(build(node, errors));
				continue;
			}

			switch (token.type) {
			case "while": {
				if (i + 1 < nodes.size()) {
					List<BadNode> whiSeq = new ArrayList<BadNode>();
					BadNode condition = nodes.get(i + 1);

					if (!(condition.type == "()" || condition.type == "(;)")) {
						String s = condition.type;

						if (condition.token != null) {
							s = condition.token.toString();
						}

						errors.add("Condition must be wrapped in brackets without the comma () or (;) at " + s);
					}

					whiSeq.add(build(condition, errors));

					if (i + 2 < nodes.size()) {
						BadNode action = nodes.get(i + 2);
						whiSeq.add(build(action, errors));
						if (action.type != "{;}") {
							String s = action.type;
							if (action.token != null) {
								s = action.token.toString();
							}
							errors.add("Action must be wrapped in {} brackets at " + s);
						}

						i += 2;
					} else {
						whiSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
						i += 1;
					}

					vseq.add(new BadNode("while", token, whiSeq));

				} else {
					errors.add("Missing condition after " + token);
				}
			}
				break;
			case "for": {

				if (i + 1 < nodes.size()) {
					List<BadNode> forSeq = new ArrayList<BadNode>();
					BadNode pcp = nodes.get(i + 1);

					if (!(pcp.type == "()" || pcp.type == "(;)")) {
						String s = pcp.type;

						if (pcp.token != null) {
							s = pcp.token.toString();
						}

						errors.add("(pre; cond; post) must be wrapped in brackets without the comma () or (;) at " + s);
					}

					if (pcp.nodes == null) {
						if (pcp.token == null) {
							errors.add("(pre; cond; post) = (null)" + token);
						} else {
							errors.add("(pre; cond; post) = (null)" + pcp.token);
						}
						forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
						forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
						forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
					} else {
						if (pcp.nodes.isEmpty()) {
							forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
							forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
							forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
						} else {
							if (pcp.nodes.size() == 1) {
								forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
								forSeq.add(build(pcp.nodes.get(0), errors));
								forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
							} else {
								if (pcp.nodes.size() == 2) {
									forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
									forSeq.add(build(pcp.nodes.get(0), errors));
									forSeq.add(build(pcp.nodes.get(1), errors));
								} else {
									forSeq.add(build(pcp.nodes.get(0), errors));
									forSeq.add(build(pcp.nodes.get(1), errors));
									forSeq.add(build(new BadNode("seq", null, pcp.nodes.subList(2, pcp.nodes.size())), errors));
								}
							}
						}
					}

					if (i + 2 < nodes.size()) {
						BadNode action = nodes.get(i + 2);
						forSeq.add(build(action, errors));
						if (action.type != "{;}") {
							String s = action.type;
							if (action.token != null) {
								s = action.token.toString();
							}
							errors.add("Action must be wrapped in {} brackets at " + s);
						}

						i += 2;
					} else {
						forSeq.add(new BadNode("{;}", null, new ArrayList<BadNode>()));
						i += 1;
					}

					vseq.add(new BadNode("for", token, forSeq));

				} else {
					errors.add("Missing (pre; cond; post) after " + token);
				}

			}
				break;
			case "continue": {
				vseq.add(new BadNode("continue", token, null));
			}
				break;
			case "break": {
				vseq.add(new BadNode("break", token, null));
			}
				break;
			case "return": {
				vseq.add(build(new BadNode("return", token, nodes.subList(i + 1, nodes.size())), errors));
				i = nodes.size();
			}
				break;
			case "if": {
				if (i + 1 < nodes.size()) {
					BadNode condition = nodes.get(i + 1);

					List<BadNode> ifSeq = new ArrayList<BadNode>();
					ifSeq.add(build(condition, errors));

					if (!(condition.type == "()" || condition.type == "(;)")) {
						String s = condition.type;

						if (condition.token != null) {
							s = condition.token.toString();
						}

						errors.add("Conditions must be wrapped in brackets without the comma () or (;) at " + s);
					}

					if (i + 2 < nodes.size()) {
						BadNode tAct = nodes.get(i + 2);
						ifSeq.add(build(tAct, errors));

						if (tAct.type != "{;}") {
							String s = tAct.type;

							if (tAct.token != null) {
								s = tAct.token.toString();
							}

							errors.add("Action must be wrapped in {} brackets at " + s);
						}

						if (i + 3 < nodes.size()) {
							BadNode els = nodes.get(i + 3);
							if (els.token != null && els.token.type == "else") {
								if (i + 4 < nodes.size()) {
									BadNode fAct = nodes.get(i + 4);
									ifSeq.add(build(fAct, errors));
									if (fAct.type != "{;}") {
										String s = fAct.type;

										if (fAct.token != null) {
											s = fAct.token.toString();
										}
										errors.add("Action must be wrapped in {} brackets at " + s);
									}
									vseq.add(new BadNode("if", token, ifSeq));
									i += 4;
								} else {
									errors.add("Missing action after " + els);
									vseq.add(new BadNode("if", token, ifSeq));
									i += 2;
								}
							} else {
								vseq.add(new BadNode("if", token, ifSeq));
								i += 2;
							}
						} else {
							vseq.add(new BadNode("if", token, ifSeq));
							i += 2;
						}

					} else {
						errors.add("Missing action after condition " + token);
					}

				} else {
					errors.add("Missing condition after " + token);
				}
			}
				break;

			default:
				vseq.add(build(node, errors));
			}

		}

		return new BadNode(vertex.type, vertex.token, vseq);

	}
}
