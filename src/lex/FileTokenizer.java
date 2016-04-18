package lex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import tmpast.node.Node;
import lex.state.State;
import lex.token.Operator;
import lex.token.Token;
import misc.Characters;

public class FileTokenizer {
    public static List<Node> split(File file, List<String> errors) throws IOException {

        State state = State.START;
        List<Token> output = new ArrayList<Token>();
        TokenBuilder tokenBuilder = new TokenBuilder();

        try (BufferedReader input = new BufferedReader(new FileReader(file))) {
            String line;

            for (int lineIndex = 1; (line = input.readLine()) != null; lineIndex++) {
                int length = line.length();
                int column = 1;
                for (int i = 0; i < length; i++) {
                    char symbol = line.charAt(i);
                    Location location = new Location(file.getName(), lineIndex, column);

                    if (Characters.isValid(symbol)) {
                        try {
                            state = state.nextState(symbol, output, tokenBuilder, location);
                        } catch (IllegalStateException exception) {
                            errors.add(exception.getMessage());
                            state = State.START;
                            tokenBuilder = new TokenBuilder();
                        }
                    } else {
                        errors.add("Invalid char code " + (int) symbol + " at " + location);
                    }

                    if (symbol == '\t') {
                        column += 4;
                    } else {
                        column += 1;
                    }

                }
                Location location = new Location(file.getName(), lineIndex, column);

                try {
                    state = state.nextState('\n', output, tokenBuilder, location);
                } catch (IllegalStateException exception) {
                    errors.add(exception.getMessage());
                    state = State.START;
                    tokenBuilder = new TokenBuilder();
                }

            }
        }

        return (List) output;
    }

}
