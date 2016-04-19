package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

import com.sun.accessibility.internal.resources.accessibility;

import ast.node.FBracketsNode;
import ast.node.ForNode;
import ast.node.IfNode;
import ast.node.Leaf;
import ast.node.Nope;
import ast.node.ArrayNode;
import ast.node.BOperatorNode;
import ast.node.CallNode;
import ast.node.RBracketsNode;
import ast.node.UOperatorNode;
import ast.node.WhileNode;
import lex.Token;
import lex.token.fold.BracketsToken;
import lex.token.fold.BracketsType;
import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import lex.token.key_word.ElseToken;
import lex.token.key_word.ForToken;
import lex.token.key_word.IfToken;
import lex.token.key_word.WhileToken;
import lex.token.pure.Operator;

public class AST {
	public static Node parse(List<Token> tokens, int priority, List<String> errors) {
		if (tokens.isEmpty()) {
			return new Nope();
		}
		int size = tokens.size();

		if (priority < 11) {

			boolean inv = priority >= 2;

			for (int i = 0; i < size; i++) {
				int index = i;
				if (inv) {
					index = size - i - 1;
				}

				Token token = tokens.get(index);

				if (token instanceof Operator) {
					Operator operator = (Operator) token;
					if (operator.priority == priority) {
						if (operator.string == ",") {
							errors.add("Use ';' as separator at " + token);
						}

						if (inv) {
							Node left = parse(tokens.subList(0, index), priority, errors);
							Node right = parse(tokens.subList(index + 1, size), priority + 1, errors);
							return new BOperatorNode(left, right, operator);
						} else {
							Node left = parse(tokens.subList(0, index), priority + 1, errors);
							Node right = parse(tokens.subList(index + 1, size), priority, errors);
							return new BOperatorNode(left, right, operator);
						}

					}

				}
			}

			return parse(tokens, priority + 1, errors);
		}

		if (size == 1) {
			Token token = tokens.get(0);
			if (token instanceof BracketsToken) {
				BracketsToken bracketsToken = (BracketsToken) token;

				if (bracketsToken.type == BracketsType.FLOWER) {
					return new FBracketsNode(parse(bracketsToken.tokens, 0, errors), bracketsToken);
				}

				if (bracketsToken.type != BracketsType.ROUND) {
					errors.add("Unexpected brackets token " + bracketsToken);
				}

				return new RBracketsNode(parse(bracketsToken.tokens, 0, errors), bracketsToken);
			}

			return new Leaf(token);
		}

		Token first = tokens.get(0);
		if (first instanceof Operator) {
			Operator operator = (Operator) first;
			if (operator.priority == 11) {
				return new UOperatorNode(parse(tokens.subList(1, size), priority, errors), operator);
			}
		}

		Token last = tokens.get(size - 1);
		if (last instanceof BracketsToken) {
			BracketsToken bracketsToken = (BracketsToken) last;
			if (bracketsToken.type == BracketsType.SQUARE) {
				Node array = parse(tokens.subList(0, size - 1), priority, errors);
				Node index = parse(bracketsToken.tokens, 0, errors);
				return new ArrayNode(array, index, bracketsToken);
			}
		}

		if (size == 2) {
			try {
				VarToken varToken = (VarToken) first;
				BracketsToken bracketsToken = (BracketsToken) last;
				if (bracketsToken.type != BracketsType.ROUND) {
					errors.add("Brackets must be round type " + bracketsToken);
				}
				return new CallNode(varToken, split(bracketsToken, errors));
			} catch (ClassCastException fake) {
			}
		}

		try {
			ForToken forToken = (ForToken) tokens.get(0);
			BracketsToken preStPost = (BracketsToken) tokens.get(1);
			if (preStPost.type != BracketsType.ROUND) {
				errors.add("Brackets must be round type " + preStPost);
			}
			Node action = parse(tokens.subList(2, size), priority, errors);
			List<Node> vars = split(preStPost, errors);

			if (vars.isEmpty()) {
				return new ForNode(forToken, action);
			}

			if (vars.size() == 1) {
				return new ForNode(forToken, vars.get(0), action);
			}

			if (vars.size() == 2) {
				return new ForNode(forToken, vars.get(0), vars.get(1), action);
			}

			if (vars.size() > 3) {
				errors.add("Too many nodes in () token " + preStPost);
			}
			return new ForNode(forToken, vars.get(0), vars.get(1), vars.get(2), action);

		} catch (ClassCastException fake) {
		}

		try {
			WhileToken whileToken = (WhileToken) tokens.get(0);
			BracketsToken state = (BracketsToken) tokens.get(1);
			if (state.type != BracketsType.ROUND) {
				errors.add("Brackets must be round type " + state);
			}
			Node action = parse(tokens.subList(2, size), priority, errors);

			if (state.tokens.isEmpty()) {
				return new WhileNode(whileToken, action);
			} else {
				return new WhileNode(whileToken, parse(state.tokens, 0, errors), action);
			}

		} catch (ClassCastException fake) {
		}

		try {
			IfToken ifToken = (IfToken) tokens.get(0);
			BracketsToken state = (BracketsToken) tokens.get(1);

			BracketsToken x = (BracketsToken) tokens.get(2);
			ElseToken elseToken = (ElseToken) tokens.get(3);
			BracketsToken y = (BracketsToken) tokens.get(2);

			if (state.type != BracketsType.ROUND) {
				errors.add("Brackets must be round type " + state);
			}

			if (x.type != BracketsType.FLOWER) {
				errors.add("Brackets must be  {} type " + x);
			}
			if (y.type != BracketsType.FLOWER) {
				errors.add("Brackets must be  {} type " + y);
			}

			return new IfNode(ifToken, parseRB(state, errors), parseFB(x, errors), parseFB(y, errors));
		} catch (ClassCastException | IndexOutOfBoundsException fake) {
		}

		for (Token token : tokens) {
			System.err.print(token.toTokenString() + " ");
		}
		System.err.println();
		return new Nope();
	}

	public static FBracketsNode parseFB(BracketsToken token, List<String> errors) {
		if (token.type != BracketsType.FLOWER) {
			errors.add("Expected {} brackets at " + token);
		}
		return new FBracketsNode(AST.parse(token.tokens, 0, errors), token);
	}

	public static RBracketsNode parseRB(BracketsToken token, List<String> errors) {
		if (token.type != BracketsType.ROUND) {
			errors.add("Expected () brackets at " + token);
		}
		return new RBracketsNode(AST.parse(token.tokens, 0, errors), token);
	}

	public static List<Node> split(BracketsToken bracketsToken, List<String> errors) {
		if (bracketsToken.type != BracketsType.ROUND) {
			errors.add("Expected () brackets at " + bracketsToken);
		}

		List<Node> vars = new ArrayList<Node>();

		List<Token> tokens = bracketsToken.tokens;
		int l = 0, n = tokens.size();

		while (l < n) {
			int r = l;
			while (r < n && !isSep(tokens.get(r))) {
				++r;
			}
			vars.add(parse(tokens.subList(l, r), 0, errors));
			l = r + 1;
		}

		return vars;
	}

	public static boolean isSep(Token token) {
		if (token instanceof Operator) {
			Operator operator = (Operator) token;
			return operator.string == ",";
		}
		return false;
	}

}
