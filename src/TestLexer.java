import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import tmpast.TapeFold;
import tmpast.node.Node;
import lex.FileTokenizer;
import lex.token.Token;

public class TestLexer {

    public static void main(String[] args) throws IOException {
        String input = "test.src";
        List<String> errors = new ArrayList<String>();

        List<Node> nodes = FileTokenizer.split(new File(input), errors);
        PrintWriter out = new PrintWriter(System.out);

        nodes = TapeFold.filterComments(nodes);
        nodes = TapeFold.foldStrings(nodes, errors);
        nodes = TapeFold.foldTypes(nodes, errors);
        nodes = TapeFold.foldBrackets(nodes, errors);

        TapeFold.print(nodes, out);

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
