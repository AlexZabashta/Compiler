import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import lex.FileTokenizer;
import lex.TapeFold;
import lex.Token;
import lex.token.ConstValueToken;
import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import lex.token.pure.SimpleString;
import misc.Characters;
import ast.Function;
import ast.Headers;
import ast.InitFunction;
import code.var.ConstVariable;
import exception.Log;
import exception.ParseException;
import exception.UnexpectedVoidType;

public class Translator {

    public static List<Function> translate(String path, String pac, Queue<SimpleString> pacs, List<ConstVariable> vals, String debug, Log log) throws IOException, ParseException {
        return translate(path, new SimpleString(pac, null), pacs, vals, debug, log);
    }

    public static List<Function> translate(String path, SimpleString pac, Queue<SimpleString> pacs, List<ConstVariable> vals, String debug, Log log) throws IOException, ParseException {
        if (pac.string.isEmpty()) {
            log.addException(new ParseException("Empty pac name ", pac));
            return new ArrayList<Function>();
        }

        for (char c : pac.string.toCharArray()) {
            if (!Characters.isChar(c)) {
                log.addException(new ParseException("Invalid char in pac name", pac));
                return new ArrayList<Function>();
            }
        }

        String outFolder = null;
        if (debug != null) {
            outFolder = debug + File.separator + pac.string + File.separator;
            File debugFolder = new File(outFolder);
            if (debugFolder.exists()) {
                if (!debugFolder.isDirectory()) {
                    throw new RuntimeException(outFolder + " isn't folder");
                }
            } else {
                debugFolder.mkdirs();
            }
        }

        List<Token> tokens = null;
        try {
            tokens = FileTokenizer.split(new File(path + pac.string + ".src"), log);
        } catch (FileNotFoundException exception) {
            log.addException(new ParseException(exception.getMessage(), pac));
            return new ArrayList<Function>();
        }
        tokens = TapeFold.filterComments(tokens);
        tokens = TapeFold.foldStrings(tokens, log);

        if (pacs != null) {
            for (Token token : tokens) {
                try {
                    VarToken varToken = (VarToken) token;
                    if (varToken.pac != null) {
                        pacs.add(varToken.pac);
                    }
                    continue;
                } catch (ClassCastException ignore) {
                }
            }
        }
        if (vals != null) {
            for (Token token : tokens) {
                try {
                    ConstValueToken val = (ConstValueToken) token;
                    try {
                        val.variable = new ConstVariable(val);
                        if (vals != null) {
                            vals.add(val.variable);
                        }
                    } catch (UnexpectedVoidType neverHappen) {
                        throw new RuntimeException(neverHappen);
                    }
                    continue;
                } catch (ClassCastException ignore) {
                }
            }
        }

        tokens = TapeFold.foldTypes(tokens, log);
        tokens = TapeFold.foldBrackets(tokens, log);

        if (outFolder != null) {
            try (PrintWriter out = new PrintWriter(new File(outFolder + "1_lex.txt"))) {
                TapeFold.print(tokens, out);
            }
        }

        List<Function> functions = Headers.foldGlobal(tokens, pac, log);

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

            try (PrintWriter out = new PrintWriter(new File(debug + File.separator + "3_ast.txt"))) {
                for (Function function : functions) {
                    out.print(function.name.toTokenString());
                    for (DeclarationToken token : function.vars) {
                        out.print(' ');
                        out.print(token.toTokenString());
                    }
                    out.println();
                    function.action.printTree(out, 1);
                }
            }
            try (PrintWriter out = new PrintWriter(new File(debug + File.separator + "4_fun.txt"))) {
                for (Function function : functions) {
                    function.println(out, 0);
                }
            }
        }

        return functions;
    }
}
