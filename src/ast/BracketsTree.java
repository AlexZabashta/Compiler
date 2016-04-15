package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lex.Token;

public class BracketsTree {
	public static BadNode build(List<Token> list, List<String> errors) {

		Stack<BadNode> stack = new Stack<BadNode>();
		stack.push(new BadNode("root", new ArrayList<BadNode>()));

		for (Token token : list) {
			switch (token.type) {
			case "#":
				break;
			case "(":
				stack.push(new BadNode("()", token, new ArrayList<BadNode>()));
				break;
			case "{":
				stack.push(new BadNode("{}", token, new ArrayList<BadNode>()));
				break;
			case "[":
				stack.push(new BadNode("[]", token, new ArrayList<BadNode>()));
				break;
			case "}":
				if (stack.size() <= 1) {
					errors.add("Negative balance of brackets at " + token);
					break;
				}
				BadNode figSeq = stack.pop();
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
				BadNode braSeq = stack.pop();
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
				BadNode sqSeq = stack.pop();
				if (sqSeq.type != "[]") {
					errors.add("Expected '" + sqSeq.type.charAt(1) + "' at " + token);
					break;
				}
				stack.peek().nodes.add(sqSeq);
				break;
			default:
				stack.peek().nodes.add(new BadNode(token));
			}
		}

		if (stack.size() != 1) {
			errors.add("Not enough closing brackets in " + list.get(0).location.file);

			while (stack.size() != 1) {
				BadNode top = stack.pop();
				stack.peek().nodes.add(top);
			}
		}

		return stack.pop();
	}
}
