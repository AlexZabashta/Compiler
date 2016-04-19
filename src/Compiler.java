import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import lex.FileTokenizer;
import lex.TapeFold;
import lex.Token;
import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import misc.Characters;
import misc.EnumType;
import ast.Headers;
import ast.Function;
import ast.InitFunction;

public class Compiler {

	static String defaultDebug = "debug";
	static String defaultResult = "result.asm";

	public static void createFolder(String name) throws IOException {
		File folder = new File(name);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	public static void main(String[] args) {

		try {
			String enter = "test";
			if (args.length >= 1) {
				enter = args[0];
			}

			String result = defaultResult;
			if (args.length >= 2) {
				result = args[1];
			}

			String debug = defaultDebug;
			if (args.length >= 3) {
				debug = args[2];
			}
			if (debug != null) {
				createFolder(debug);
			}

			List<DeclarationToken> vars = new ArrayList<DeclarationToken>();
			Map<String, DeclarationToken> varMap = new HashMap<String, DeclarationToken>();

			List<Function> funs = new ArrayList<Function>();
			Map<String, Function> funMap = new HashMap<String, Function>();

			List<InitFunction> inits = new ArrayList<InitFunction>();
			Function mainf = null;

			// List<Variable> vars = new ArrayList<Variable>();
			// List<FuncitonHeader> headers = new ArrayList<FuncitonHeader>();

			Set<String> pacs = new HashSet<String>();
			Queue<String> queue = new ArrayDeque<String>();

			enter = enter.toLowerCase();

			if (enter.length() >= 4 && enter.substring(enter.length() - 4).equals(".src")) {
				enter = enter.substring(0, enter.length() - 4);
			}

			queue.add(enter);

			boolean hasErrors = false;
			List<String> errors = new ArrayList<String>();

			while (!queue.isEmpty()) {
				String pac = queue.poll().toLowerCase().intern();
				if (pacs.add(pac)) {

					try {
						System.err.println("Parse '" + pac + "' sourse:");

						if (pac.isEmpty()) {
							throw new Exception("Empty sourse name.");
						}

						for (char c : pac.toCharArray()) {
							if (c == '.' || !Characters.isChar(c)) {
								throw new Exception("Invalid chars in name \"" + Characters.escape(pac) + "\"");
							}
						}

						List<Token> tokens = FileTokenizer.split(new File(pac + ".src"), errors);
						tokens = TapeFold.filterComments(tokens);

						tokens = TapeFold.foldStrings(tokens, errors);

						for (Token token : tokens) {
							try {
								VarToken varToken = (VarToken) token;
								queue.add(varToken.pac.string);
							} catch (ClassCastException | NullPointerException fake) {
							}
						}

						tokens = TapeFold.foldTypes(tokens, errors);
						tokens = TapeFold.foldBrackets(tokens, errors);

						hasErrors |= printErrorsAndClear("Lexer errors:", errors);

						String outFolder = null;
						if (debug != null) {
							outFolder = debug + File.separatorChar + pac + File.separatorChar;
							createFolder(outFolder);
						}

						if (outFolder != null) {
							try (PrintWriter out = new PrintWriter(new File(outFolder + "1_lex.txt"))) {
								TapeFold.print(tokens, out);
							}
						}

						List<Function> functions = Headers.foldGlobal(tokens, pac, errors);
						if (outFolder != null) {
							try (PrintWriter out = new PrintWriter(new File(outFolder + "2_dec.txt"))) {

								for (Function function : functions) {
									if (function instanceof InitFunction) {
										InitFunction initFunction = (InitFunction) function;
										for (DeclarationToken token : initFunction.vars) {
											out.println(token);
										}
									}
								}

								for (Function function : functions) {
									out.println(function.type + " " + function);
								}
							}
						}

						for (Function function : functions) {
							if (function instanceof InitFunction) {
								InitFunction initFunction = (InitFunction) function;
								for (DeclarationToken token : initFunction.vars) {
									{
										String key = token.varToken.toTokenString();
										DeclarationToken dtoken = varMap.get(key);
										if (dtoken == null) {
											varMap.put(key, token);
											vars.add(token);
										} else {
											errors.add("Double varible declaration " + token + " and " + dtoken);
										}
									}

								}
							}
							{
								String key = function.toString();
								Function val = funMap.get(key);

								if (val == null) {
									funMap.put(key, function);
									funs.add(function);
								} else {
									errors.add("Double function declaration " + function.name + " and " + val.name);
								}
							}

							if (function.toString().equals(enter + ".main" + Characters.typeSeparator + "char.2")) {
								mainf = function;
							}
						}

						hasErrors |= printErrorsAndClear("AST errors:", errors);

						if (hasErrors) {
							throw new Exception(pac + " has errors");
						}
						System.err.println("Success!");
					} catch (Exception exception) {
						System.err.println(exception.getMessage());
					}
				}
			}

			System.err.println("Build program:");

			if (mainf == null) {
				errors.add("Can't find void " + enter + ".main(char.2) function");
			} else {
				if (mainf.type.type != EnumType.VOID) {
					errors.add("The 'main' must be void function");
				}
			}
			hasErrors |= printErrorsAndClear("Builder errors:", errors);

			if (debug != null) {
				try (PrintWriter out = new PrintWriter(new File(debug + File.separator + "ast.txt"))) {
					for (Function function : funs) {
						out.print(function.name.toTokenString());
						for (DeclarationToken token : function.vars) {
							out.print(' ');
							out.print(token.toTokenString());
						}
						out.println();
						function.action.printTree(out, 1);
					}
				}
			}

			if (hasErrors) {
				throw new Exception("Program has errors");
			}
			System.err.println("Success!");

		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}

	}

	static boolean printErrorsAndClear(String step, List<String> errors) {
		if (errors.isEmpty()) {
			return false;
		}
		System.err.println("    " + step);
		for (String error : errors) {
			System.err.println("    " + "    " + error);
		}
		// System.err.println();
		errors.clear();
		return true;
	}
}
