package code;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import code.act.Action;
import lex.Token;
import ast.Node;

public class Program {

	public static void getListing(List<String> external) {

	}

	public static void build(Node vertex, String pac, List<Variable> vars, List<Funciton> funs, List<String> external, List<String> errors) {
		if (vertex.type != "root") {
			throw new RuntimeException("It is not root node.");
		}

		if (vertex.nodes == null) {
			throw new RuntimeException("Root nodes is null.");
		}

		for (Node node : vertex.nodes) {
			List<Node> nodes = node.nodes;
			if (nodes != null) {
				if (nodes.isEmpty()) {
					continue;
				}
				if (nodes.size() == 2) {
					Node glob = nodes.get(0);
					if (glob.token == null) {
						errors.add("Token of def node is null " + glob.type);
					} else {
						if (glob.token.type != "str" || !glob.token.text.equals("global")) {
							errors.add("Can't find global def " + glob.token);
						} else {
							Variable.getVarsDef(pac, nodes.get(1), vars, errors);
						}
					}

				} else if (nodes.size() == 4) {
					try {
						List<Variable> fType = new ArrayList<Variable>();
						fType.add(Variable.build(nodes.get(0).token, pac, nodes.get(1).token));
						Variable.getVarsDef(null, nodes.get(2), fType, errors);
						List<Action> listing = new ArrayList<Action>();
						Funciton.build(nodes.get(3), listing, errors);
						funs.add(new Funciton(fType, listing));
					} catch (RuntimeException error) {
						if (vertex.token == null) {
							errors.add(error.getMessage() + " at " + vertex.type);
						} else {
							errors.add(error.getMessage() + " at " + vertex.token);
						}
					}

				} else {
					String types = "";

					if (node.token == null) {
						types += " " + node.type;
					} else {
						types += " " + node.token;
					}

					types += ":";

					for (Node part : nodes) {
						if (part.token == null) {
							types += " " + part.type;
						} else {
							types += " " + part.token;
						}
					}
					errors.add("Unknown definition in " + types);
				}
			} else {
				errors.add("Def nodes is null.");
			}
		}
	}
}
