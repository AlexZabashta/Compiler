import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lex.FileTokenizer;
import lex.TapeFold;
import lex.Token;

public class TestLexer {

    public static void main(String[] args) throws IOException {
        String input = "test.src";
        List<String> errors = new ArrayList<String>();

        List<Token> tokens = FileTokenizer.split(new File(input), errors);
        PrintWriter out = new PrintWriter(System.out);

        tokens = TapeFold.filterComments(tokens);
        tokens = TapeFold.foldStrings(tokens, errors);
        tokens = TapeFold.foldTypes(tokens, errors);
        tokens = TapeFold.foldBrackets(tokens, errors);

        TapeFold.print(tokens, out);

        out.close();

        printErrorsAndClear("Lex", errors);

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
