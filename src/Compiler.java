import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import code.FunctionZone;
import code.VisibilityZone;
import exception.Log;
import lex.FileTokenizer;
import lex.TapeFold;
import lex.Token;
import lex.token.ConstValue;
import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import lex.token.key_word.BoolToken;
import lex.token.pure.CharToken;
import lex.token.pure.NumberToken;
import lex.token.pure.QuotedString;
import lex.token.pure.SimpleString;
import misc.Characters;
import misc.EnumType;
import misc.LittleEndian;
import misc.Type;
import ast.Headers;
import ast.Function;
import ast.InitFunction;
import ast.SystemFunction;
import ast.node.leaf.QuotedStringNode;

public class Compiler {

    static String srcPath = "";
    static String defaultDebug = "debug";
    static String defaultResult = "result";

    public static void createFolder(String name) throws IOException {
        File folder = new File(name);
        if (folder.exists()) {
            folder.delete();
        }
        folder.mkdir();
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

            List<byte[]> vals = new ArrayList<byte[]>();

            List<DeclarationToken> vars = new ArrayList<DeclarationToken>();
            Map<String, DeclarationToken> varMap = new HashMap<String, DeclarationToken>();

            List<Function> funs = new ArrayList<Function>();
            Map<String, Function> funMap = new HashMap<String, Function>();

            List<InitFunction> inits = new ArrayList<InitFunction>();
            Function mainf = null;

            // List<Variable> vars = new ArrayList<Variable>();
            // List<FuncitonHeader> headers = new ArrayList<FuncitonHeader>();

            Set<String> pacs = new HashSet<String>();
            Queue<SimpleString> queue = new ArrayDeque<SimpleString>();

            enter = enter.toLowerCase();

            if (enter.length() >= 4 && enter.substring(enter.length() - 4).equals(".src")) {
                enter = enter.substring(0, enter.length() - 4);
            }

            String sys = "sys";
            if (enter.equals(sys)) {
                throw new RuntimeException("Main pac can't be \"sys\"");
            }

            queue.add(new SimpleString(enter, null));

            Log log = new Log(true);
            List<String> errors = new ArrayList<String>();

            List<String> programData = new ArrayList<>();
            List<String> programText = new ArrayList<>();

            try {
                System.err.println("Load '" + sys + ".asm':");

                boolean text = true;

                try (BufferedReader reader = new BufferedReader(new FileReader(new File("sys.asm")))) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (line.isEmpty()) {
                            continue;
                        }

                        if (line.startsWith(";text")) {
                            line = line.substring(";text ".length());

                            String[] str = line.split(" ");

                            Type ftype = Type.get(str[0], Integer.parseInt(str[1]));

                            String fname = str[2];

                            int alen = (str.length - 3) / 2;

                            Type[] atype = new Type[alen];

                            for (int i = 0; i < alen; i++) {
                                atype[i] = Type.get(str[i * 2 + 3], Integer.parseInt(str[i * 2 + 4]));
                            }

                            Function sysFun = new SystemFunction(ftype, fname, atype);

                            funMap.put(sysFun.toString(), sysFun);

                            programText.add(";" + line);
                            text = true;
                            continue;
                        }

                        if (line.startsWith(";data")) {
                            text = false;
                            continue;
                        }

                        if (text) {
                            programText.add(line);
                        } else {
                            programData.add(line);
                        }

                    }
                }

                // for (Function function : StandardFunctions.getFunctions()) {
                // funMap.put(function.toString(), function);
                // }

                if (printErrorsAndClear("Loader errors:", errors)) {
                    throw new Exception("sys.asm has errors");
                }

                System.err.println("Success!");
            } catch (Exception exception) {
                System.err.println(exception.getMessage());
            }

            while (sys != null) {
                while (!queue.isEmpty()) {
                    SimpleString pac = queue.poll();
                    if (pacs.add(pac.string)) {

                        System.err.println("Parse '" + pac + "' sourse:");

                        List<Function> functions = ParseFile.parse(srcPath, pac, queue, vals, debug, log);
                        log.printErrorsAndClear("Lexer errors:");

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
                                            errors.add("Duplicate global variable " + token + " and " + dtoken);
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
                                    errors.add("Duplicate function " + function.name + " and " + val.name);
                                }
                            }

                            if (function.toString().equals(enter + ".main")) {
                                mainf = function;
                            }
                        }

                        System.err.println("Success!");
                        printErrorsAndClear("AST errors:", errors);

                    }
                }

                queue.add(new SimpleString(sys, null));
                sys = null;
            }
            System.err.println("Build program:");

            if (mainf == null) {
                errors.add("Can't find void " + enter + ".main() function");
            } else {
                if (mainf.type.type != EnumType.VOID) {
                    errors.add("The 'main' must be void function");
                }
            }

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
                try (PrintWriter out = new PrintWriter(new File(debug + File.separator + "fun.txt"))) {
                    for (Function function : funs) {
                        function.println(out, 0);
                    }
                }
            }

            for (Function function : funs) {
                FunctionZone visibilityZone = function.getVisibilityZone(varMap, funMap, log);

                if (debug != null) {
                    try (PrintWriter out = new PrintWriter(new File(debug + File.separator + function + " fun.txt"))) {
                        visibilityZone.println(out, 2);
                    }
                }

                if (function == mainf) {
                    programText.add("_main:");
                    programText.add("        pusha");
                }

                // visibilityZone.asm(programText);

                if (function == mainf) {
                    programText.add("        popa");
                    programText.add("        xor eax, eax");
                }
                programText.add("    ret");
                programText.add("");

            }

            if (printErrorsAndClear("Builder errors:", errors)) {
                throw new Exception("Program has errors");
            }
            System.err.println("Success!");

            for (int i = 0; i < vals.size(); i++) {
                byte[] array = vals.get(i);

                StringBuilder builder = new StringBuilder();

                builder.append("    val");
                builder.append(i);
                builder.append(" db ");

                boolean sep = false;

                for (byte b : array) {
                    if (sep) {
                        builder.append(',');
                    }
                    sep = true;

                    if (b > 0) {
                        char c = (char) b;

                        if (c != '\'' && c != '\"' && c != '\\') {
                            if ((' ' <= c && c <= ']') || ('a' <= c && c <= '}')) {
                                builder.append("'");
                                builder.append(c);
                                builder.append("'");
                                continue;
                            }
                        }
                    }
                    builder.append(b);
                }
                programData.add(builder.toString());
            }

            try {
                System.err.println("Write '" + enter + ".asm':");

                try (PrintWriter out = new PrintWriter(new File(enter + ".asm"))) {

                    out.println("global _main");
                    out.println("extern _printf");
                    out.println("extern _getchar");
                    out.println("extern _malloc");
                    out.println("extern _putchar");
                    out.println();
                    out.println("section .text");

                    for (String line : programText) {
                        out.println("    " + line);
                    }

                    out.println();

                    out.println("section .data");
                    for (String line : programData) {
                        out.println("    " + line);
                    }
                }

                System.err.println("Success!");
            } catch (Exception exception) {
                System.err.println(exception.getMessage());
            }

        } catch (

        Exception exception)

        {
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
