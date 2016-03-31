package code;

import java.util.List;

import ast.Node;
import lex.Location;
import lex.Token;
import misc.Types;

public class Variable {
	public int type;
	public Location location;
	public String name;
	public String pac;

	public static void getVarsDef(String pac, Node vertex, List<Variable> vars, List<String> errors) {
		if (!(vertex.type == "(,)" || vertex.type == "()")) {
			errors.add("Vertex type must br (,)");
		}

		try {
			for (Node node : vertex.nodes) {
				if (node.type != "seq") {
					throw new RuntimeException("Sub node type must be seq " + node.type);
				}
				if (!node.nodes.isEmpty()) {
					vars.add(Variable.build(node.nodes.get(0).token, pac, node.nodes.get(1).token));
				}
			}

		} catch (RuntimeException error) {
			if (vertex.token == null) {
				errors.add(error.getMessage() + " at " + vertex.type);
			} else {
				errors.add(error.getMessage() + " at " + vertex.token);
			}
		}
	}

	public static Variable build(Token typeT, String pac, Token nameT) {
		if (typeT.type != "str") {
			throw new RuntimeException("Variable type must be 'str' " + typeT);
		}

		if (nameT.type != "str") {
			throw new RuntimeException("Variable type must be 'str' " + typeT);
		}

		String name = nameT.text;

		String[] str = name.split("\\.");

		if (str.length > 2) {
			throw new RuntimeException("Var name contain too many points " + nameT);
		}

		if (str.length == 2) {
			if (pac == null) {
				throw new RuntimeException("Expected local varible at " + nameT);
			}

			if (pac.equals("*")) {
				pac = str[0];
			}

			if (!pac.equals(str[0])) {
				throw new RuntimeException("Expected pac name " + pac + " at " + nameT);
			}
			name = str[1];
		}

		return new Variable(Types.getType(typeT.text), nameT.location, pac, name);
	}

	public Variable(int type, Location location, String pac, String name) {
		this.type = type;
		this.location = location;
		if (name.isEmpty()) {
			throw new RuntimeException("Empty name");
		}
		if (pac != null) {
			this.pac = pac.intern();
		}
		this.name = name.intern();
	}

	@Override
	public String toString() {

		String string = "var(" + type + ") ";
		if (pac != null) {
			string += pac + ".";
		}
		string += name;

		if (location != null) {
			string += " from " + location.toString();
		}

		return string;
	}
}
