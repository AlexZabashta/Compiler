import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import code.FuncitonHeader;
import code.Function;
import code.Program;
import code.Variable;
import ast.AST;
import ast.BracketsTree;
import ast.CommaTree;
import ast.KeyWordsTree;
import ast.BadNode;
import ast.OperatorTree;
import lex.FileTokenizer;
import lex.Token;
import misc.Characters;
import misc.KeyWords;

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
			List<Variable> vars = new ArrayList<Variable>();
			List<FuncitonHeader> headers = new ArrayList<FuncitonHeader>();
			List<String> errors = new ArrayList<String>();

			Set<String> pacs = new HashSet<String>();
			Queue<String> queue = new ArrayDeque<String>();

			enter = enter.toLowerCase();

			if (enter.length() >= 4 && enter.substring(enter.length() - 4).equals(".src")) {
				enter = enter.substring(0, enter.length() - 4);
			}

			queue.add(enter);

			boolean hasErrors = false;

			while (!queue.isEmpty()) {
				String pac = queue.poll().toLowerCase().intern();
				if (pacs.add(pac)) {
					System.err.println("Parse '" + pac + "' sourse:");

					if (pac.isEmpty()) {
						throw new Exception("Empty sourse name.");
					}

					for (char c : pac.toCharArray()) {
						if (c == '.' || !Characters.isChar(c)) {
							throw new Exception("Invalid chars in name " + pac);
						}
					}

					List<Token> list = FileTokenizer.split(new File(pac + ".src"), errors);
					hasErrors |= printErrorsAndClear("Lexer errors:", errors);

					String outFolder = null;
					if (debug != null) {
						outFolder = debug + File.separatorChar + pac + File.separatorChar;
						createFolder(outFolder);
					}

					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "1_lex.txt"))) {
							FileTokenizer.print(list, out);
						}
					}

					for (Token token : list) {
						if (token.type == "str") {
							String[] varName = token.text.split("\\.");

							if (varName.length > 2) {
								errors.add("Var name contain too many points at " + token);
							}

							if (varName.length == 2) {
								if (varName[0].isEmpty()) {
									errors.add("Empty sourse name at " + token);
								} else {
									queue.add(varName[0]);
								}
							}
						}
					}
					hasErrors |= printErrorsAndClear("Loader errors:", errors);

					// extract(Set<String> external, List<String> errors)

					BadNode bt = BracketsTree.build(list, errors);
					hasErrors |= printErrorsAndClear("Brackets tree errors:", errors);
					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "2_bt.txt"))) {
							bt.print(out);
						}
					}

					BadNode ct = CommaTree.build(bt, errors);
					hasErrors |= printErrorsAndClear("Separators errors:", errors);
					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "3_ct.txt"))) {
							ct.print(out);
						}
					}

					BadNode kt = KeyWordsTree.build(ct, errors);
					hasErrors |= printErrorsAndClear("Key words errors:", errors);
					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "4_kt.txt"))) {
							kt.print(out);
						}
					}

					BadNode ot = OperatorTree.build(kt, errors);
					hasErrors |= printErrorsAndClear("Operators errors:", errors);
					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "5_ot.txt"))) {
							ot.print(out);
						}
					}

					if (hasErrors) {
						throw new Exception(pac + " has errors.");
					}

					List<Variable> curVars = new ArrayList<Variable>();
					List<FuncitonHeader> curHeads = new ArrayList<FuncitonHeader>();
					Program.build(ot, pac, curVars, curHeads, errors);

					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "6_vf.txt"))) {
							for (Variable var : curVars) {
								out.println(var);
							}

							out.println();

							for (FuncitonHeader header : curHeads) {
								header.print(out);
							}
						}
					}
					{
						int size = curVars.size();

						for (int i = 0; i < size; i++) {
							Variable vi = curVars.get(i);
							for (int j = 0; j < i; j++) {
								Variable vj = curVars.get(j);

								if (vi.name.equals(vj.name)) {
									errors.add("Find redefinition of global variable '" + vi.name + "' at " + vi.location + " and " + vj.location);
								}
							}
						}
					}

					{
						int size = curHeads.size();

						for (int i = 0; i < size; i++) {
							FuncitonHeader hi = curHeads.get(i);
							for (int j = 0; j < i; j++) {
								FuncitonHeader hj = curHeads.get(j);

								if (hi.name == hj.name) {
									errors.add("Find redefinition of function " + hi.type + " " + hj.type);
								}

							}
						}
					}

					hasErrors |= printErrorsAndClear("Global definitions errors:", errors);
					if (hasErrors) {
						throw new Exception(pac + ".src has errors.");
					}

					System.err.println("Success!");
					System.err.println();

					vars.addAll(curVars);
					headers.addAll(curHeads);
				}
			}

			System.err.println("Build program:");

			List<FuncitonHeader> mainHeaders = new ArrayList<FuncitonHeader>();

			boolean enterHasMain = false;

			{
				for (int i = headers.size() - 1; i >= 0; i--) {
					FuncitonHeader header = headers.get(i);
					if (header.type.size() == 1) {
						Variable var = header.type.get(0);
						if (var.type == 0 && var.name == "main") {
							mainHeaders.add(header);
							if (enter.equals(var.pac)) {
								enterHasMain = true;
							}
						}
					}

				}
			}

			if (!enterHasMain) {
				throw new RuntimeException("Can't find 'void main' in " + enter);
			}

			List<Function> functions = new ArrayList<Function>();
			for (FuncitonHeader header : headers) {
				functions.add(header.build(vars, headers, errors));
			}

			hasErrors |= printErrorsAndClear("Functions errors definitions errors:", errors);
			if (debug != null) {
				try (PrintWriter out = new PrintWriter(new File(debug + File.separator + "fun.txt"))) {
					for (Function function : functions) {
						function.print(out);
					}
				}

			}

			System.err.println("Success!");
			System.err.println();

		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}

	}

	static boolean printErrorsAndClear(String step, List<String> errors) {
		if (errors.isEmpty()) {
			return false;
		}
		System.err.println(step);
		for (String error : errors) {
			System.err.println("    " + error);
		}
		System.err.println();
		errors.clear();
		return true;
	}
}
