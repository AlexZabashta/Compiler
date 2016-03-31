package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lex.Token;

public class BracketsTree {
	public static Node build(List<Token> list, List<String> errors) {

		Stack<Node> stack = new Stack<Node>();
		stack.push(new Node("root", new ArrayList<Node>()));

		for (Token token : list) {
			switch (token.type) {
			case "#":
				break;
			case "(":
				stack.push(new Node("()", token, new ArrayList<Node>()));
				break;
			case "{":
				stack.push(new Node("{}", token, new ArrayList<Node>()));
				break;
			case "[":
				stack.push(new Node("[]", token, new ArrayList<Node>()));
				break;
			case "}":
				if (stack.size() <= 1) {
					errors.add("Negative balance of brackets at " + token);
					break;
				}
				Node figSeq = stack.pop();
				if (figSeq.type != "{}") {
					errors.add("Expected '" + figSeq.type.charAt(1) + "' at " + token);
					break;
				}
				stack.peek().nodes.add(figSeq);
				break;
			case ")":
				if (stack.size() <= 1) {
					errors.add("Negative balance of brackets at " + token);
					break;
				}
				Node braSeq = stack.pop();
				if (braSeq.type != "()") {
					errors.add("Expected '" + braSeq.type.charAt(1) + "' at " + token);
					break;
				}
				stack.peek().nodes.add(braSeq);
				break;
			case "]":
				if (stack.size() <= 1) {
					errors.add("Negative balance of brackets at " + token);
					break;
				}
				Node sqSeq = stack.pop();
				if (sqSeq.type != "[]") {
					errors.add("Expected '" + sqSeq.type.charAt(1) + "' at " + token);
					break;
				}
				stack.peek().nodes.add(sqSeq);
				break;
			default:
				stack.peek().nodes.add(new Node(token));
			}
		}

		if (stack.size() != 1) {
			errors.add("Not enough closing brackets in " + list.get(0).location.file);

			while (stack.size() != 1) {
				Node top = stack.pop();
				stack.peek().nodes.add(top);
			}
		}

		return stack.pop();
	}
}
