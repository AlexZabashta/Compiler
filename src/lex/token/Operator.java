package lex.token;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import lex.Location;

public class Operator extends Token {

	private static final Map<String, Integer> operators = new HashMap<String, Integer>();
	static {
		operators.put("[", 13);
		operators.put("]", 13);
		operators.put("(", 13);
		operators.put(")", 13);
		operators.put("{", 13);
		operators.put("}", 13);

		operators.put(".", 12);

		operators.put("#", 11);
		operators.put("~", 11);

		operators.put("*", 10);
		operators.put("/", 10);
		operators.put("%", 10);
		operators.put(":", 10);

		operators.put("+", 9);
		operators.put("-", 9);

		operators.put("<", 8);
		operators.put("<=", 8);
		operators.put(">", 8);
		operators.put(">=", 8);

		operators.put("<>", 7);
		operators.put("==", 7);

		operators.put("&", 6);
		operators.put("^", 5);
		operators.put("|", 4);
		operators.put("&&", 3);
		operators.put("||", 2);

		operators.put("=", 1);

		operators.put(";", 0);
		operators.put(",", 0);
	}
	public static boolean isOperator(char symbol) {
		return isOperator(Character.toString(symbol));
	}

	public static boolean isOperator(String string) {
		return operators.containsKey(string);
	}

	public static int priorityOf(String operator) {
		Integer priority = operators.get(operator);
		if (priority == null) {
			return 14;
		} else {
			return priority;
		}
	}

	public final int priority;

	public final String string;

	public Operator(String string, Location location) {
		super(location);
		this.string = string.intern();
		this.priority = operators.get(this.string);
	}

	@Override
	public void printToken(PrintWriter out) {
		out.print(string);
	}

}
