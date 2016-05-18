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

import lex.token.fold.DeclarationToken;
import lex.token.pure.SimpleString;
import misc.EnumType;
import asm.Programm;
import ast.Function;
import ast.InitFunction;
import code.FunctionZone;
import code.var.ConstVariable;
import code.var.GlobalVariable;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.UnexpectedVoidType;

public class Compiler {

    public static Programm compile(String srcFolder, String enter, String debug, Log log) throws IOException, ParseException {
        Programm programm = new Programm();

        List<DeclarationToken> vars = new ArrayList<DeclarationToken>();
        Map<String, GlobalVariable> varMap = new HashMap<String, GlobalVariable>();

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

        String sys = StandardFunctions.PAC;
        if (enter.equals(sys)) {
            throw new RuntimeException("Main pac can't be \"sys\"");
        }

        SimpleString enterToken = new SimpleString(enter, null);

        queue.add(enterToken);

        List<String> programData = new ArrayList<>();
        List<String> programText = new ArrayList<>();

        List<ConstVariable> vals = new ArrayList<ConstVariable>();

        while (sys != null) {
            while (!queue.isEmpty()) {
                SimpleString pac = queue.poll();
                if (pacs.add(pac.string)) {

                    // System.err.println("Parse '" + pac + "' sourse:");

                    List<Function> functions = Translator.translate(srcPath, pac, queue, vals, debug, log);

                    for (Function function : functions) {
                        if (function instanceof InitFunction) {
                            InitFunction initFunction = (InitFunction) function;
                            for (DeclarationToken token : initFunction.vars) {
                                String key = token.varToken.toTokenString();
                                try {
                                    if (varMap.put(key, new GlobalVariable(token)) == null) {
                                        vars.add(token);
                                    } else {
                                        log.addException(new SemanticException("Duplicate global variable", token));
                                    }
                                } catch (UnexpectedVoidType exception) {
                                    log.addException(new SemanticException(exception.getMessage(), token));
                                }
                            }
                        }
                        {
                            String key = function.toString();

                            if (funMap.put(key, function) == null) {
                                funs.add(function);
                            } else {
                                log.addException(new SemanticException("Duplicate function declaration", function.name));
                            }
                        }

                        if (function.toString().equals(enter + ".main")) {
                            mainf = function;
                        }
                    }
                    // System.err.println("Success!");
                }
            }

            queue.add(new SimpleString(sys, null));
            sys = null;
        }
        // System.err.println("Build program:");

        if (mainf == null) {
            log.addException(new SemanticException("Can't find void " + enter + ".main() function", enterToken));
        } else {
            if (mainf.type.type != EnumType.VOID) {
                log.addException(new SemanticException("The 'main' must be void function", enterToken));
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

        List<FunctionZone> functionZones = new ArrayList<FunctionZone>();

        for (Function function : funs) {
            functionZones.add(function.getVisibilityZone(varMap, funMap, log));
        }
        if (debug != null) {
            try (PrintWriter out = new PrintWriter(new File(debug + File.separator + "code.txt"))) {
                for (FunctionZone zone : functionZones) {
                    zone.println(out, 0);
                    out.println();
                    out.println();
                }
            }
        }
        return programm;
    }

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

    public static void main(String[] args) throws IOException {

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

            Log log = new Log(true);
            compile("", enter, debug, log);
            log.printErrorsAndClear("");

        } catch (ParseException exception) {
            exception.printStackTrace();
        }

    }

}
