package lex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lex.state.State;
import misc.Characters;
import exception.Log;
import exception.ParseException;
import exception.SyntaxesException;

public class FileTokenizer {
    public static List<Token> split(File file, Log log) throws IOException, ParseException {

        State state = State.START;
        List<Token> output = new ArrayList<Token>();
        TokenBuilder tokenBuilder = new TokenBuilder();

        try (BufferedReader input = new BufferedReader(new FileReader(file))) {
            String line;

            for (int lineIndex = 1; (line = input.readLine()) != null; lineIndex++) {
                int length = line.length();
                int column = 1;
                for (int i = 0; i <= length; i++) {
                    char symbol = i == length ? '\n' : line.charAt(i);
                    Location location = new Location(file.getName(), lineIndex, column);

                    if (Characters.isValid(symbol)) {
                        try {
                            state = state.nextState(symbol, output, tokenBuilder, location);
                        } catch (SyntaxesException exception) {
                            log.addException(exception);
                            state = State.START;
                            tokenBuilder = new TokenBuilder();
                        }
                    } else {
                        log.addException(new SyntaxesException("Invalid char", "code " + ((int) symbol), location));
                    }

                    if (symbol == '\t') {
                        column += 4;
                    } else {
                        column += 1;
                    }

                }

            }
        }

        return output;
    }

}
