package lex;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lex.token.fold.BracketsToken;
import lex.token.fold.BracketsType;
import lex.token.fold.BreakToken;
import lex.token.fold.DeclarationToken;
import lex.token.fold.TypeToken;
import lex.token.fold.VarToken;
import lex.token.key_word.ElseToken;
import lex.token.key_word.ForToken;
import lex.token.key_word.IfToken;
import lex.token.key_word.InitToken;
import lex.token.key_word.BoolToken;
import lex.token.key_word.ReturnToken;
import lex.token.key_word.WhileToken;
import lex.token.pure.Comment;
import lex.token.pure.NumberToken;
import lex.token.pure.Operator;
import lex.token.pure.SimpleString;
import misc.EnumType;
import misc.Type;

public class TapeFold {

	public static List<Token> foldBrackets(List<Token> tokens, List<String> errors) {

		Stack<BracketsToken> stack = new Stack<BracketsToken>();
		stack.add(new BracketsToken(BracketsType.FLOWER, null));

		int size = tokens.size();

		for (int i = 0; i < size; i++) {
			Token token = tokens.get(i);
			try {

				try {
					Operator operator = (Operator) token;
					BracketsType type = BracketsType.get(operator.string);
					if (type == null) {
						throw new ClassCastException("Not breaket");
					}

					if (BracketsType.isOpen(operator.string)) {
						stack.push(new BracketsToken(type, operator.location));
					} else {
						if (stack.size() == 1) {
							errors.add("Negative balance of brackets at " + operator);
						} else {
							BracketsToken bracketsToken = stack.pop();
							if (bracketsToken.type != type) {
								errors.add("Expected '" + BracketsType.close(bracketsToken.type) + "' breaket at " + operator);
							}
							stack.peek().tokens.add(bracketsToken);
						}
					}

				} catch (ClassCastException fake) {
					stack.peek().tokens.add(token);
				}

			} catch (RuntimeException exception) {
				errors.add(exception.getMessage() + " at " + token);
			}
		}

		if (stack.size() != 1) {
			errors.add("Not enough closing brackets at " + stack.peek());

			while (stack.size() != 1) {
				BracketsToken bracketsToken = stack.pop();
				stack.peek().tokens.add(bracketsToken);
			}

		}

		return stack.peek().tokens;
	}

	public static List<Token> foldTypes(List<Token> tokens, List<String> errors) {
		List<Token> list = new ArrayList<Token>();

		int size = tokens.size();

		for (int i = 0; i < size; i++) {
			Token token = tokens.get(i);
			try {
				if (token instanceof TypeToken) {
					TypeToken typeToken = (TypeToken) token;

					if (i + 1 < size && (tokens.get(i + 1) instanceof VarToken)) {
						VarToken varToken = (VarToken) tokens.get(i + 1);
						list.add(new DeclarationToken(typeToken, varToken));
						++i;
					} else {
						errors.add("Expected varible after type " + token);
					}
				} else {
					list.add(token);
				}

			} catch (RuntimeException exception) {
				errors.add(exception.getMessage() + " at " + token);
			}
		}

		return list;

	}

	public static List<Token> foldStrings(List<Token> tokens, List<String> errors) {
		List<Token> list = new ArrayList<Token>();

		int size = tokens.size();

		for (int i = 0; i < size; i++) {
			Token token = tokens.get(i);
			try {

				if (token instanceof Operator) {
					Operator operator = (Operator) token;
					if (operator.string == ".") {
						throw new RuntimeException("Missing token after '.'");
					}
				}

				if (token instanceof SimpleString) {
					SimpleString str = (SimpleString) token;

					switch (str.string) {
					case "true": {
						list.add(new BoolToken(true, str.location));
					}
						break;
					case "false": {
						list.add(new BoolToken(false, str.location));
					}
						break;
					case "init": {
						list.add(new InitToken(str.location));
					}
						break;
					case "while": {
						list.add(new WhileToken(str.location));
					}
						break;
					case "for": {
						list.add(new ForToken(str.location));
					}
						break;
					case "if": {
						list.add(new IfToken(str.location));
					}
						break;
					case "else": {
						list.add(new ElseToken(str.location));
					}
						break;
					case "return": {
						list.add(new ReturnToken(str.location));
					}
						break;
					case "break": {
						try {
							Operator operator = (Operator) tokens.get(i + 1);
							if (operator.string != ".") {
								throw new ClassCastException();
							}
							NumberToken number = (NumberToken) tokens.get(i + 2);
							i += 2;

							try {
								list.add(new BreakToken(number.number, str.location));
							} catch (RuntimeException error) {
								list.add(new BreakToken(1, str.location));
								throw error;
							}

						} catch (ClassCastException | IndexOutOfBoundsException fake) {
							list.add(new BreakToken(1, str.location));
						}
					}
						break;

					case "int":
					case "void":
					case "bool":
					case "char":
					case "string":
					case "text": {
						try {
							Operator operator = (Operator) tokens.get(i + 1);
							if (operator.string != ".") {
								throw new ClassCastException();
							}
							NumberToken number = (NumberToken) tokens.get(i + 2);
							i += 2;

							Type type = new Type(EnumType.VOID, 0);
							try {
								type = Type.get(str.string, number.number);
							} catch (RuntimeException error) {
								errors.add(error.getMessage() + str);
								try {
									type = Type.get(str.string, 0);
								} catch (RuntimeException error2) {
									errors.add(error2.getMessage() + str);
								}
							}

							list.add(new TypeToken(type, str.location));

						} catch (ClassCastException | IndexOutOfBoundsException fake) {
							list.add(new TypeToken(Type.get(str.string, 0), str.location));
						}

					}
						break;
					default:
						try {
							Operator operator = (Operator) tokens.get(i + 1);
							if (operator.string != ".") {
								throw new ClassCastException();
							}

							SimpleString pac = str;
							SimpleString name = (SimpleString) tokens.get(i + 2);
							i += 2;

							list.add(new VarToken(pac, name));

						} catch (ClassCastException | IndexOutOfBoundsException fake) {
							list.add(new VarToken(null, str));
						}
					}

				} else {
					list.add(token);
				}
			} catch (RuntimeException exception) {
				errors.add(exception.getMessage() + " at " + token);
			}
		}

		return list;
	}

	public static void print(List<Token> list, PrintWriter out) throws IOException {
		int tab = 0;

		for (Token token : list) {
			if (token instanceof Operator) {
				Operator operator = (Operator) token;
				if (operator.priority == Operator.priorityOf("(")) {
					if (operator.string == "{" | operator.string == "[" | operator.string == "(") {
						token.print(out, tab++);
					} else {
						token.print(out, --tab);
					}
					continue;
				}
			}
			token.print(out, tab);
		}
	}

	// public static List<Token> foldTypes(List<Token> tokens) {
	// List<Token> list = new ArrayList<Token>();
	//
	// for (Token token : tokens) {
	// if (token instanceof KeyWord) {
	// KeyWord keyWord = (KeyWord) token;
	//
	// }
	// list.add(token);
	// }
	// return list;
	//
	// }

	public static List<Token> filterComments(List<Token> tokens) {
		List<Token> list = new ArrayList<Token>();

		for (Token token : tokens) {
			if (token instanceof Comment) {
				continue;
			}
			list.add(token);
		}
		return list;

	}

	// public static List<Token> foldKeyWords(List<Token> tokens) {
	// List<Token> list = new ArrayList<Token>();
	//
	// for (Token token : tokens) {
	// if (token instanceof SimpleString) {
	// SimpleString simpleString = (SimpleString) token;
	// if (KeyWord.isKeyWord(simpleString.string)) {
	// list.add(new KeyWord(simpleString));
	// continue;
	// }
	// }
	// list.add(token);
	// }
	//
	// return list;
	//
	// }
}
