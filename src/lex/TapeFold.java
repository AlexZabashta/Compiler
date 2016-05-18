package lex;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lex.token.fold.BracketsToken;
import lex.token.fold.BracketsType;
import lex.token.fold.BreakToken;
import lex.token.fold.DeclarationToken;
import lex.token.fold.TypeToken;
import lex.token.fold.VarToken;
import lex.token.key_word.BoolToken;
import lex.token.key_word.ElseToken;
import lex.token.key_word.ForToken;
import lex.token.key_word.IfToken;
import lex.token.key_word.InitToken;
import lex.token.key_word.ReturnToken;
import lex.token.pure.Comment;
import lex.token.pure.NumberToken;
import lex.token.pure.Operator;
import lex.token.pure.SimpleString;
import misc.Type;
import exception.Log;
import exception.ParseException;
import exception.SyntaxesException;
import exception.TypeInitException;

public class TapeFold {

    public static List<Token> foldBrackets(List<Token> tokens, Log log) throws ParseException {

        Stack<BracketsToken> stack = new Stack<BracketsToken>();
        stack.add(new BracketsToken(BracketsType.FLOWER, null));

        int size = tokens.size();

        for (int i = 0; i < size; i++) {
            Token token = tokens.get(i);
            try {
                Operator operator = (Operator) token;
                BracketsType type = BracketsType.get(operator.string);
                if (type == null) {
                    throw new ClassCastException("Not breaket");
                }

                if (BracketsType.isOpen(operator.string)) {
                    stack.push(new BracketsToken(type, operator.location));
                } else {
                    if (stack.size() == 1) {
                        log.addException(new SyntaxesException("Negative balance of brackets", operator));
                    } else {
                        BracketsToken bracketsToken = stack.pop();
                        if (bracketsToken.type != type) {
                            log.addException(new SyntaxesException("Expected '" + BracketsType.close(bracketsToken.type) + "' breaket", operator));
                        }
                        stack.peek().tokens.add(bracketsToken);
                    }
                }

            } catch (ClassCastException fake) {
                stack.peek().tokens.add(token);
            }
        }

        if (stack.size() != 1) {
            log.addException(new SyntaxesException("Not enough closing brackets", stack.peek()));
            while (stack.size() != 1) {
                BracketsToken bracketsToken = stack.pop();
                stack.peek().tokens.add(bracketsToken);
            }

        }

        return stack.peek().tokens;
    }

    public static List<Token> foldTypes(List<Token> tokens, Log log) throws ParseException {
        List<Token> list = new ArrayList<Token>();

        int size = tokens.size();

        for (int i = 0; i < size; i++) {
            Token token = tokens.get(i);

            if (token instanceof TypeToken) {
                TypeToken typeToken = (TypeToken) token;

                if (i + 1 < size && (tokens.get(i + 1) instanceof VarToken)) {
                    VarToken varToken = (VarToken) tokens.get(i + 1);
                    list.add(new DeclarationToken(typeToken, varToken));
                    ++i;
                } else {
                    log.addException(new SyntaxesException("Expected varible after type", token));
                }
            } else {
                list.add(token);
            }

        }

        return list;

    }

    public static List<Token> foldStrings(List<Token> tokens, Log log) throws ParseException {
        List<Token> list = new ArrayList<Token>();

        int size = tokens.size();

        for (int i = 0; i < size; i++) {
            Token token = tokens.get(i);

            if (token instanceof Operator) {
                Operator operator = (Operator) token;
                if (operator.string == ".") {
                    log.addException(new SyntaxesException("Missing token after '.'", token));
                    continue;
                }
            }

            if (token instanceof SimpleString) {
                SimpleString str = (SimpleString) token;

                switch (str.string) {
                case "true": {
                    list.add(new BoolToken(true, str.location));
                }
                    break;
                case "false": {
                    list.add(new BoolToken(false, str.location));
                }
                    break;
                case "init": {
                    list.add(new InitToken(str.location));
                }
                    break;
                case "while":
                case "for": {
                    list.add(new ForToken(str.location));
                }
                    break;
                case "if": {
                    list.add(new IfToken(str.location));
                }
                    break;
                case "else": {
                    list.add(new ElseToken(str.location));
                }
                    break;
                case "return": {
                    list.add(new ReturnToken(str.location));
                }
                    break;
                case "break": {
                    try {
                        Operator operator = (Operator) tokens.get(i + 1);
                        if (operator.string != ".") {
                            throw new ClassCastException();
                        }
                        NumberToken number = (NumberToken) tokens.get(i + 2);
                        i += 2;

                        try {
                            list.add(new BreakToken(number.number, str.location));
                        } catch (SyntaxesException error) {
                            list.add(new BreakToken(1, str.location));
                            log.addException(error);
                        }

                    } catch (ClassCastException | IndexOutOfBoundsException fake) {
                        list.add(new BreakToken(1, str.location));
                    }
                }
                    break;

                case "int":
                case "void":
                case "bool":
                case "char":
                case "string":
                case "text": {
                    try {
                        Operator operator = (Operator) tokens.get(i + 1);
                        if (operator.string != ".") {
                            throw new ClassCastException();
                        }
                        NumberToken number = (NumberToken) tokens.get(i + 2);
                        i += 2;

                        Type type = new Type();
                        try {
                            type = Type.get(str.string, number.number);
                        } catch (TypeInitException error) {
                            log.addException(new SyntaxesException(error.getMessage(), str));
                            try {
                                type = Type.get(str.string, 0);
                            } catch (TypeInitException neverHappen) {
                                throw new RuntimeException(neverHappen);
                            }
                        }
                        list.add(new TypeToken(type, str.location));
                    } catch (ClassCastException | IndexOutOfBoundsException fake) {
                        Type type = new Type();
                        try {
                            type = Type.get(str.string, 0);
                        } catch (TypeInitException neverHappen) {
                            throw new RuntimeException(neverHappen);
                        }

                        list.add(new TypeToken(type, str.location));
                    }
                }
                    break;
                default:
                    try {
                        Operator operator = (Operator) tokens.get(i + 1);
                        if (operator.string != ".") {
                            throw new ClassCastException();
                        }

                        SimpleString pac = str;
                        SimpleString name = (SimpleString) tokens.get(i + 2);
                        i += 2;

                        list.add(new VarToken(pac, name));

                    } catch (ClassCastException | IndexOutOfBoundsException fake) {
                        list.add(new VarToken(null, str));
                    }
                }

            } else {
                list.add(token);
            }

        }

        return list;
    }

    public static void print(List<Token> list, PrintWriter out) throws IOException {
        int tab = 0;

        for (Token token : list) {
            if (token instanceof Operator) {
                Operator operator = (Operator) token;
                if (operator.priority == Operator.priorityOf("(")) {
                    if (operator.string == "{" | operator.string == "[" | operator.string == "(") {
                        token.print(out, tab++);
                    } else {
                        token.print(out, --tab);
                    }
                    continue;
                }
            }
            token.print(out, tab);
        }
    }

    public static List<Token> filterComments(List<Token> tokens) {
        List<Token> list = new ArrayList<Token>();

        for (Token token : tokens) {
            if (token instanceof Comment) {
                continue;
            }
            list.add(token);
        }
        return list;

    }
}
