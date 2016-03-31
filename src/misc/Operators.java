package misc;

import java.util.HashMap;
import java.util.Map;

public class Operators {
	private static final boolean[] isOperator = new boolean[256];
	private static final Map<String, Integer> priority = new HashMap<String, Integer>();

	static {
		isOperator['<'] = true;
		isOperator['>'] = true;
		isOperator['('] = true;
		isOperator[')'] = true;
		isOperator['{'] = true;
		isOperator['}'] = true;
		isOperator['['] = true;
		isOperator[']'] = true;

		isOperator[';'] = true;
		isOperator[','] = true;

		isOperator['='] = true;
		isOperator['-'] = true;
		isOperator['+'] = true;
		isOperator['/'] = true;
		isOperator['*'] = true;
		isOperator['%'] = true;
		isOperator['^'] = true;
		isOperator['&'] = true;
		isOperator['|'] = true;
		// isOperator['~'] = true;
		isOperator[':'] = true;
		// isOperator['!'] = true;

		// !~

		// priority.put("~", 10);
		// priority.put("!", 10);
		priority.put("*", 9);
		priority.put("/", 9);
		priority.put("%", 9);
		priority.put(":", 9);
		priority.put("+", 8);
		priority.put("-", 8);
		priority.put("<", 7);
		priority.put("<=", 7);
		priority.put(">", 7);
		priority.put(">=", 7);
		priority.put("<>", 6);
		priority.put("==", 6);
		priority.put("&", 5);
		priority.put("^", 4);
		priority.put("|", 3);
		priority.put("&&", 2);
		priority.put("||", 1);
		priority.put("=", 0);

	}

	public static boolean isOperator(char symbol) {
		if (symbol < 256) {
			return isOperator[symbol];
		} else {
			return false;
		}
	}

	public static int getPriority(String op) {
		Integer p = priority.get(op);
		if (p == null) {
			return -1;
		} else {
			return p;
		}
	}

}
