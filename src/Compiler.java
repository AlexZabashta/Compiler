import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import code.Funciton;
import code.Program;
import code.Variable;
import ast.AST;
import ast.BracketsTree;
import ast.CommaTree;
import ast.KeyWordsTree;
import ast.Node;
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
			List<Funciton> funs = new ArrayList<Funciton>();
			List<String> errors = new ArrayList<String>();
			boolean hasErrors = false;

			Set<String> pacs = new HashSet<String>();
			Queue<String> queue = new ArrayDeque<String>();

			enter = enter.toLowerCase();

			if (enter.length() >= 4 && enter.substring(enter.length() - 4).equals(".src")) {
				enter = enter.substring(0, enter.length() - 4);
			}

			queue.add(enter);

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

					Node bt = BracketsTree.build(list, errors);
					hasErrors |= printErrorsAndClear("Brackets tree errors:", errors);
					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "2_bt.txt"))) {
							bt.print(out);
						}
					}

					Node ct = CommaTree.build(bt, errors);
					hasErrors |= printErrorsAndClear("Separators errors:", errors);
					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "3_ct.txt"))) {
							ct.print(out);
						}
					}

					Node kt = KeyWordsTree.build(ct, errors);
					hasErrors |= printErrorsAndClear("Key words errors:", errors);
					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "4_kt.txt"))) {
							kt.print(out);
						}
					}

					Node ot = OperatorTree.build(kt, errors);
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
					List<Funciton> curFuns = new ArrayList<Funciton>();
					List<String> external = new ArrayList<String>();
					Program.build(ot, pac, curVars, curFuns, external, errors);

					queue.addAll(external);

					hasErrors |= printErrorsAndClear("Builder errors:", errors);
					if (hasErrors) {
						throw new Exception(pac + ".src has errors.");
					}
					if (outFolder != null) {
						try (PrintWriter out = new PrintWriter(new File(outFolder + "6_vf.txt"))) {
							for (Variable var : curVars) {
								out.println(var);
							}

							out.println();

							for (Funciton fun : curFuns) {
								fun.print(out);
							}
						}
					}
					System.err.println("Success!");
					vars.addAll(curVars);
					funs.addAll(curFuns);
				}

			}
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
