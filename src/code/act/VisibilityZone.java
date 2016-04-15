package code.act;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import code.Variable;
import lex.Location;

public class VisibilityZone extends Action {

	public VisibilityZone parent;

	public final List<Action> actions = new ArrayList<Action>();
	public final List<Variable> vars = new ArrayList<Variable>();

	public VisibilityZone(String lable, Location location) {
		super(lable, location);
	}

	@Override
	public void print(int tab, PrintWriter out) {
		for (int t = 0; t < tab; t++) {
			out.print("    ");
		}
		out.print("(");
		boolean sp = false;
		for (Variable variable : vars) {
			if (sp) {
				out.print(", ");
			}
			out.print(variable.type + " " + variable.name);
			sp = true;
		}
		out.print(") {");
		out.println();

		for (int t = 0; t < tab; t++) {
			out.print("    ");
		}
		out.println("}");
	}

}
