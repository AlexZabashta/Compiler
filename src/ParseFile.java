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
import lex.token.ConstValue;
import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import lex.token.pure.SimpleString;
import misc.Characters;
import ast.Function;
import ast.Headers;
import ast.InitFunction;
import exception.Log;
import exception.ParseException;

public class ParseFile {

    public static List<Function> parse(String pac, Queue<SimpleString> pacs, List<byte[]> vals, String debug, Log log) throws IOException, ParseException {
        return parse(new SimpleString(pac, null), pacs, vals, debug, log);
    }

    public static List<Function> parse(SimpleString pac, Queue<SimpleString> pacs, List<byte[]> vals, String debug, Log log) throws IOException, ParseException {
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
            outFolder = debug + File.separator + pac + File.separator;
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
            tokens = FileTokenizer.split(new File(pac + ".src"), log);
        } catch (FileNotFoundException exception) {
            log.addException(new ParseException("Can't find pac", pac));
            return new ArrayList<Function>();
        }
        tokens = TapeFold.filterComments(tokens);
        tokens = TapeFold.foldStrings(tokens, log);

        if (pacs != null) {
            for (Token token : tokens) {
                try {
                    VarToken varToken = (VarToken) token;
                    pacs.add(varToken.pac);
                    continue;
                } catch (ClassCastException | NullPointerException fake) {
                }
            }
        }
        if (vals != null) {
            try {
                for (Token token : tokens) {
                    ConstValue val = (ConstValue) token;
                    val.setValIndex(vals.size());
                    vals.add(val.getConstValue());
                    continue;
                }
            } catch (ClassCastException fake) {
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
