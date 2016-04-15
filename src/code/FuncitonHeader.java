package code;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.Location;
import misc.Mangling;
import ast.BadNode;
import code.act.Action;
import code.act.VisibilityZone;

public class FuncitonHeader {
	public final List<Variable> type;
	public final BadNode root;
	public final String name;

	public Function build(List<Variable> gvars, List<FuncitonHeader> fheaders, List<String> errors) {
		VisibilityZone zone = new VisibilityZone(null, root.location());

		try {
			if (root.type != "{;}") {
				throw new RuntimeException("Action vertex type must br {;}");
			}
			build(Variable.temp(0), root, gvars, fheaders, type.subList(1, type.size()), true, zone, errors);

		} catch (RuntimeException error) {
			if (root.token == null) {
				errors.add(error.getMessage() + " at " + root.type);
			} else {
				errors.add(error.getMessage() + " at " + root.token);
			}
		}
		return new Function(this, zone);
	}

	// int getType(Node vertex, VisibilityZone curZone, List<Variable> gvars, List<FuncitonHeader> fheaders) {
	// if (vertex == null) {
	// return 0;
	// }
	//
	// sw
	// }

	int findType(String name, List<Variable> fvars, VisibilityZone curZone) {
		for (Variable v : fvars) {
			if (v.name.equals(name)) {
				return v.type;
			}
		}

		while (curZone != null) {
			for (Variable v : curZone.vars) {
				if (v.name.equals(name)) {
					return v.type;
				}
			}
			curZone = curZone.parent;
		}
		return -1;
	}

	public void build(Variable var, BadNode vertex, List<Variable> gvars, List<FuncitonHeader> fheaders, List<Variable> lvars, boolean nz, VisibilityZone curZone, List<String> errors) {
		if (vertex == null) {
			return;
		}

		try {
			switch (vertex.type) {

			case "=": {
				BadNode left = vertex.nodes.get(0);
				BadNode right = vertex.nodes.get(1);

				if (left.nodes.size() == 2 && left.nodes.get(0).token.type == "str" && left.nodes.get(1).token.type == "str") {

				} else {
					int p = left.nodes.size() - 1;

					while (p >= 0 && left.nodes.get(p).type == "[;]") {
						--p;
					}

					if (p != 0 || left.nodes.get(0).token.type != "str") {
						throw new RuntimeException("Unnoun type of left side");
					}

					String vName = left.nodes.get(0).token.text;
					String[] sName = vName.split("\\.");
					if (sName.length == 1) {
						int type = findType(vName, lvars, curZone);
						if (type == -1) {
							throw new RuntimeException("Can't find " + vName);
						}

						
						
						
					} else {
						int type = -1;
						for (Variable gv : gvars) {
							if (gv.pac.equals(sName[0]) && gv.name.equals(sName[1])) {
								type = gv.type;
								break;
							}
						}
						if (type == -1) {
							throw new RuntimeException("Can't find " + vName);
						}

					}
				}

			}
				break;

			case "{;}":
			case "[;]":
			case "(;)":
			case "()":
				if (nz) {
					VisibilityZone subZone = new VisibilityZone(null, vertex.location());
					curZone.actions.add(subZone);
					subZone.parent = curZone;
					curZone = subZone;
				}
			case "seq": {
				List<BadNode> nodes = vertex.nodes;

				if (nodes == null || nodes.isEmpty()) {
					return;
				}

				int size = nodes.size();

				for (int i = 0; i < size; i++) {
					if (i == size - 1) {
						build(var, nodes.get(i), gvars, fheaders, lvars, nz, curZone, errors);
					} else {
						build(Variable.temp(0), nodes.get(i), gvars, fheaders, lvars, nz, curZone, errors);
					}
				}
			}
				break;
			default:
				throw new RuntimeException("Unexpected vertex type");
			}

		} catch (RuntimeException error) {
			if (vertex.token == null) {
				errors.add(error.getMessage() + " at " + vertex.type);
			} else {
				errors.add(error.getMessage() + " at " + vertex.token);
			}
		}
	}

	public FuncitonHeader(List<Variable> type, BadNode root) {
		this.type = type;
		this.root = root;

		String tname = type.get(0).pac + "." + type.get(0).name;

		for (int i = 1; i < type.size(); i++) {
			tname += '.' + Mangling.convert(type.get(i).type);
		}

		this.name = tname.intern();
	}

	public void print(PrintWriter out) {
		out.println("fun " + name + " " + type.get(0));

		for (int i = 1; i < type.size(); i++) {
			out.println("    arg " + type.get(i));
		}

		// out.println("        " + root.header());

	}
}
