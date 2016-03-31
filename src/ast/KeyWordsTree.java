package ast;

import java.util.ArrayList;
import java.util.List;

import lex.Token;
import misc.KeyWords;

public class KeyWordsTree {
	public static Node build(Node vertex, List<String> errors) {

		if (vertex == null) {
			errors.add("Find null vertex.");
			return new Node("seq", null, new ArrayList<Node>());
		}

		List<Node> nodes = vertex.nodes;

		if (nodes == null) {
			Token token = vertex.token;
			if (token != null) {
				if (KeyWords.contains(token.type)) {
					errors.add("Unexpected key word " + token);
				}
			}
			return vertex;
		}

		List<Node> vseq = new ArrayList<Node>();

		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);

			Token token = node.token;
			if (token == null) {
				vseq.add(build(node, errors));
				continue;
			}

			switch (token.type) {
			case "while": {
				if (i + 1 < nodes.size()) {
					List<Node> whiSeq = new ArrayList<Node>();
					Node condition = nodes.get(i + 1);

					if (!(condition.type == "()" || condition.type == "(;)")) {
						String s = condition.type;

						if (condition.token != null) {
							s = condition.token.toString();
						}

						errors.add("Condition must be wrapped in brackets without the comma () or (;) at " + s);
					}

					whiSeq.add(build(condition, errors));

					if (i + 2 < nodes.size()) {
						Node action = nodes.get(i + 2);
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
						whiSeq.add(new Node("{;}", null, new ArrayList<Node>()));
						i += 1;
					}

					vseq.add(new Node("while", token, whiSeq));

				} else {
					errors.add("Missing condition after " + token);
				}
			}
				break;
			case "for": {

				if (i + 1 < nodes.size()) {
					List<Node> forSeq = new ArrayList<Node>();
					Node pcp = nodes.get(i + 1);

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
						forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
						forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
						forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
					} else {
						if (pcp.nodes.isEmpty()) {
							forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
							forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
							forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
						} else {
							if (pcp.nodes.size() == 1) {
								forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
								forSeq.add(build(pcp.nodes.get(0), errors));
								forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
							} else {
								if (pcp.nodes.size() == 2) {
									forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
									forSeq.add(build(pcp.nodes.get(0), errors));
									forSeq.add(build(pcp.nodes.get(1), errors));
								} else {
									forSeq.add(build(pcp.nodes.get(0), errors));
									forSeq.add(build(pcp.nodes.get(1), errors));
									forSeq.add(build(new Node("seq", null, pcp.nodes.subList(2, pcp.nodes.size())), errors));
								}
							}
						}
					}

					if (i + 2 < nodes.size()) {
						Node action = nodes.get(i + 2);
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
						forSeq.add(new Node("{;}", null, new ArrayList<Node>()));
						i += 1;
					}

					vseq.add(new Node("for", token, forSeq));

				} else {
					errors.add("Missing (pre; cond; post) after " + token);
				}

			}
				break;
			case "continue": {
				vseq.add(new Node("continue", token, null));
			}
				break;
			case "break": {
				vseq.add(new Node("break", token, null));
			}
				break;
			case "return": {
				vseq.add(build(new Node("return", token, nodes.subList(i + 1, nodes.size())), errors));
				i = nodes.size();
			}
				break;
			case "if": {
				if (i + 1 < nodes.size()) {
					Node condition = nodes.get(i + 1);

					List<Node> ifSeq = new ArrayList<Node>();
					ifSeq.add(build(condition, errors));

					if (!(condition.type == "()" || condition.type == "(;)")) {
						String s = condition.type;

						if (condition.token != null) {
							s = condition.token.toString();
						}

						errors.add("Conditions must be wrapped in brackets without the comma () or (;) at " + s);
					}

					if (i + 2 < nodes.size()) {
						Node tAct = nodes.get(i + 2);
						ifSeq.add(build(tAct, errors));

						if (tAct.type != "{;}") {
							String s = tAct.type;

							if (tAct.token != null) {
								s = tAct.token.toString();
							}

							errors.add("Action must be wrapped in {} brackets at " + s);
						}

						if (i + 3 < nodes.size()) {
							Node els = nodes.get(i + 3);
							if (els.token != null && els.token.type == "else") {
								if (i + 4 < nodes.size()) {
									Node fAct = nodes.get(i + 4);
									ifSeq.add(build(fAct, errors));
									if (fAct.type != "{;}") {
										String s = fAct.type;

										if (fAct.token != null) {
											s = fAct.token.toString();
										}
										errors.add("Action must be wrapped in {} brackets at " + s);
									}
									vseq.add(new Node("if", token, ifSeq));
									i += 4;
								} else {
									errors.add("Missing action after " + els);
									vseq.add(new Node("if", token, ifSeq));
									i += 2;
								}
							} else {
								vseq.add(new Node("if", token, ifSeq));
								i += 2;
							}
						} else {
							vseq.add(new Node("if", token, ifSeq));
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

		return new Node(vertex.type, vertex.token, vseq);

	}
}
