package code;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ast.Node;
import code.act.Action;

public class Funciton {
	public final List<Variable> type;
	public final List<Action> listing;

	public static void build(Node vertex, List<Action> listing, List<String> errors) {

	}

	public Funciton(List<Variable> type, List<Action> listing) {
		this.type = type;
		this.listing = listing;
	}

	public void print(PrintWriter out) {
		out.println("fun " + type.get(0));

		for (int i = 1; i < type.size(); i++) {
			out.println("    arg " + type.get(i));
		}

		for (Action action : listing) {
			out.println("        " + action);
		}

		out.println();

	}
}
