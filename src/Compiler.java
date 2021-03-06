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
import asm.Command;
import asm.Programm;
import ast.Function;
import ast.InitFunction;
import ast.SystemFunction;
import code.Environment;
import code.FunctionZone;
import code.act.CallFunction;
import code.var.ConstVariable;
import code.var.GlobalVariable;
import code.var.LocalVariable;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.TypeMismatch;
import exception.UnexpectedVoidType;

public class Compiler {

    public static Programm compile(String srcFolder, String enter, String debug, Log log) throws IOException, ParseException {
        Programm programm = new Programm();

        List<GlobalVariable> vars = new ArrayList<GlobalVariable>();
        List<Function> funs = new ArrayList<Function>();
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

        String sys = SystemFunction.PAC;
        if (enter.equals(sys)) {
            throw new RuntimeException("Main pac can't be \"sys\"");
        }

        SimpleString enterToken = new SimpleString(enter, null);

        queue.add(enterToken);

        List<ConstVariable> vals = new ArrayList<ConstVariable>();

        Environment environment = new Environment();
        funs.addAll(StandardFunctions.getFunctions());

        for (Function function : funs) {
            try {
                environment.addFunction(function.toString(), function);
            } catch (DeclarationException e) {
                throw new RuntimeException(e);
            }
        }

        while (sys != null) {
            if (queue.isEmpty() && sys != null) {
                queue.add(new SimpleString(sys, null));
                sys = null;
            }
            while (!queue.isEmpty()) {
                SimpleString pac = queue.poll();
                if (pacs.add(pac.string)) {

                    // System.err.println("Parse '" + pac + "' sourse:");

                    List<Function> functions = Translator.translate(srcPath, pac, queue, vals, debug, log);

                    for (Function function : functions) {
                        if (function instanceof InitFunction) {
                            InitFunction initFunction = (InitFunction) function;

                            for (GlobalVariable variable : initFunction.globalVariables) {
                                try {
                                    String name = variable.location;
                                    environment.addGlobalVar(name, variable);
                                    vars.add(variable);
                                } catch (DeclarationException exception) {
                                    log.addException(new SemanticException(exception.getMessage(), initFunction.name));
                                }
                            }
                            inits.add(initFunction);
                        } else {
                            funs.add(function);
                        }
                        {
                            try {
                                String name = function.toString();
                                environment.addFunction(name, function);
                            } catch (DeclarationException exception) {
                                log.addException(new SemanticException(exception.getMessage(), function.name));
                            }
                        }

                        if (function.toString().equals(enter + ".main")) {
                            mainf = function;
                        }
                    }
                    // System.err.println("Success!");
                }
            }
        }
        // System.err.println("Build program:");

        if (mainf == null) {
            log.addException(new SemanticException("Can't find void " + enter + ".main() function", enterToken));
            return programm;
        }

        if (mainf.type.type != EnumType.VOID) {
            log.addException(new SemanticException("The 'main' must be void function", enterToken));
        }

        String start = null;

        int initLen = inits.size();
        List<FunctionZone> functionZones = new ArrayList<FunctionZone>();

        for (int i = initLen - 1; i >= 0; i--) {
            Function function = inits.get(i);

            if (start == null) {
                start = function.toString();
            }

            FunctionZone zone = function.getVisibilityZone(environment, log);
            functionZones.add(zone);

            Function next = mainf;

            if (i > 0) {
                next = inits.get(i - 1);
            }

            try {
                zone.addAction(new CallFunction(null, next, new ArrayList<Variable>(), null, null));
            } catch (TypeMismatch | NullPointerException ignore) {
            }
        }

        if (start == null) {
            start = mainf.toString();
        }

        for (Function function : funs) {
            FunctionZone zone = function.getVisibilityZone(environment, log);
            functionZones.add(zone);
        }

        if (debug != null) {
            for (FunctionZone zone : functionZones) {
                String name = zone.label;
                try (PrintWriter out = new PrintWriter(new File(debug + File.separator + name + ".txt"))) {
                    zone.println(out, 1);
                    out.println();
                    out.println();
                }
            }

        }

        Map<String, List<Command>> asmFunctions = new HashMap<>();

        for (FunctionZone zone : functionZones) {
            List<Command> commands = new ArrayList<>();
            String label = zone.asm(commands);
            AsmOptimizer.removeNop(commands);
            asmFunctions.put(label, commands);
        }

        asmFunctions = AsmOptimizer.removeDeadCode(start, asmFunctions);
        try (PrintWriter out = new PrintWriter(new File(enter + ".asm"))) {

            out.println("global _main");
            out.println("extern _printf");
            out.println("extern _getchar");
            out.println("extern _malloc");
            out.println("extern _putchar");
            out.println("extern _free");

            {

                out.println();
                out.println("section .data");

                // emptyarray
                out.println("    emptyarray:  dd (emptyarray + 4), -1, 0");

                for (GlobalVariable globalVariable : vars) {
                    if (globalVariable.type.dim == 0) {
                        out.println("    " + globalVariable.location + ":  dd 0");
                    } else {
                        out.println("    " + globalVariable.location + ":  dd (emptyarray + 4)");
                    }
                }

                for (ConstVariable constVariable : vals) {
                    if (constVariable.bigData != null) {
                        String location = constVariable.location;
                        out.print("    " + location + ":  dd ");
                        out.print("(" + location + " + 4), -1, " + constVariable.smallData);

                        out.println(constVariable.bigData);
                    }
                }
            }
            {
                out.println();
                out.println("section .text");

                out.println("    _main:");
                out.println("         pusha");

                out.println("         call " + start);

                out.println("         popa");
                out.println("         xor eax, eax");
                out.println("         ret");

                for (List<Command> commands : asmFunctions.values()) {
                    for (Command command : commands) {
                        command.printYASM_WIN_32(out, 6);
                    }
                    out.println();
                }
            }

        }

        try (PrintWriter out = new PrintWriter(new File(enter + ".bat"))) {

            String asmfile = enter + ".asm";
            String objfile = enter + ".obj";
            String exefile = enter + ".exe";

            out.println("@echo off");
            out.println("del " + objfile);
            out.println("del " + exefile);

            out.println("echo YASM (from asm to obj):");

            out.println("call yasm\\yasm.exe -f win32 " + asmfile + " -o " + objfile);
            out.println("echo.%space%");
            out.println("echo GCC (from obj to exe):");
            out.println("call E:\\Code\\MinGW\\bin\\gcc.exe " + objfile + " -o " + exefile);
            out.println("echo.%space%");
            out.println("echo RUN EXE:");
            out.println("echo.%space%");
            out.println("call " + exefile);
            out.println("echo.%space%");
            out.println("");
            out.println("pause");
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
