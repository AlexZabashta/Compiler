package tmpast;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import tmpast.node.BracketsNode;
import tmpast.node.BracketsType;
import tmpast.node.BreakNode;
import tmpast.node.DeclarationNode;
import tmpast.node.Node;
import tmpast.node.TypeNode;
import tmpast.node.VarNode;
import tmpast.node.key_word.ElseToken;
import tmpast.node.key_word.ForToken;
import tmpast.node.key_word.IfToken;
import tmpast.node.key_word.InitToken;
import tmpast.node.key_word.ReturnToken;
import tmpast.node.key_word.WhileToken;
import tmpast.type.EnumType;
import tmpast.type.Type;
import lex.token.Comment;
import lex.token.Operator;
import lex.token.SimpleString;
import lex.token.Token;
import lex.token.Number;
import misc.KeyWords;

public class TapeFold {

    public static List<Node> foldBrackets(List<Node> nodes, List<String> errors) {

        Stack<BracketsNode> stack = new Stack<BracketsNode>();
        stack.add(new BracketsNode(BracketsType.FLOWER, null));

        int size = nodes.size();

        for (int i = 0; i < size; i++) {
            Node node = nodes.get(i);
            try {

                try {
                    Operator operator = (Operator) node;
                    BracketsType type = BracketsType.get(operator.string);
                    if (type == null) {
                        throw new ClassCastException("Not breaket");
                    }

                    if (BracketsType.isOpen(operator.string)) {
                        stack.push(new BracketsNode(type, operator.location));
                    } else {
                        if (stack.size() == 1) {
                            errors.add("Negative balance of brackets at " + operator);
                        } else {
                            BracketsNode bracketsNode = stack.pop();
                            if (bracketsNode.type != type) {
                                errors.add("Expected '" + BracketsType.close(bracketsNode.type) + "' breaket at " + operator);
                            }
                            stack.peek().nodes.add(bracketsNode);
                        }
                    }

                } catch (ClassCastException fake) {
                    stack.peek().nodes.add(node);
                }

            } catch (RuntimeException exception) {
                errors.add(exception.getMessage() + " at " + node);
            }
        }

        if (stack.size() != 1) {
            errors.add("Not enough closing brackets in " + stack.peek().location);

            while (stack.size() != 1) {
                BracketsNode bracketsNode = stack.pop();
                stack.peek().nodes.add(bracketsNode);
            }

        }

        return stack.peek().nodes;
    }

    public static List<Node> foldTypes(List<Node> nodes, List<String> errors) {
        List<Node> list = new ArrayList<Node>();

        int size = nodes.size();

        for (int i = 0; i < size; i++) {
            Node node = nodes.get(i);
            try {
                if (node instanceof TypeNode) {
                    TypeNode typeNode = (TypeNode) node;

                    if (i + 1 < size && (nodes.get(i + 1) instanceof VarNode)) {
                        VarNode varNode = (VarNode) nodes.get(i + 1);
                        list.add(new DeclarationNode(typeNode, varNode));
                        ++i;
                    } else {
                        errors.add("Expected varible after type " + node);
                    }
                } else {
                    list.add(node);
                }

            } catch (RuntimeException exception) {
                errors.add(exception.getMessage() + " at " + node);
            }
        }

        return list;

    }

    public static List<Node> foldStrings(List<Node> nodes, List<String> errors) {
        List<Node> list = new ArrayList<Node>();

        int size = nodes.size();

        for (int i = 0; i < size; i++) {
            Node node = nodes.get(i);
            try {

                if (node instanceof Operator) {
                    Operator operator = (Operator) node;
                    if (operator.string == ".") {
                        throw new RuntimeException("Unexpected " + node);
                    }
                }

                if (node instanceof SimpleString) {
                    SimpleString str = (SimpleString) node;

                    switch (str.string) {
                    case "init": {
                        list.add(new InitToken(str.location));
                    }
                    case "while": {
                        list.add(new WhileToken(str.location));
                    }
                        break;
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
                            Number number = (Number) nodes.get(i + 1);
                            ++i;
                            try {
                                list.add(new BreakNode(number.number, str.location));
                            } catch (RuntimeException error) {
                                list.add(new BreakNode(1, str.location));
                                throw error;
                            }
                        } catch (ClassCastException | IndexOutOfBoundsException fake) {
                            list.add(new BreakNode(1, str.location));
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
                            Operator operator = (Operator) nodes.get(i + 1);
                            if (operator.string != ".") {
                                throw new ClassCastException();
                            }
                            Number number = (Number) nodes.get(i + 2);
                            i += 2;

                            Type type = new Type(EnumType.VOID, 0);
                            try {
                                type = Type.get(str.string, number.number);
                            } catch (RuntimeException error) {
                                errors.add(error.getMessage() + " at " + str.location);
                                try {
                                    type = Type.get(str.string, 0);
                                } catch (RuntimeException error2) {
                                    errors.add(error2.getMessage() + " at " + str.location);
                                }
                            }

                            list.add(new TypeNode(type, str.location));

                        } catch (ClassCastException | IndexOutOfBoundsException fake) {
                            list.add(new TypeNode(Type.get(str.string, 0), str.location));
                        }

                    }
                        break;
                    default:
                        try {
                            Operator operator = (Operator) nodes.get(i + 1);
                            if (operator.string != ".") {
                                throw new ClassCastException();
                            }

                            SimpleString pac = str;
                            SimpleString name = (SimpleString) nodes.get(i + 2);
                            i += 2;

                            list.add(new VarNode(pac, name));

                        } catch (ClassCastException | IndexOutOfBoundsException fake) {
                            list.add(new VarNode(null, str));
                        }
                    }

                } else {
                    list.add(node);
                }
            } catch (RuntimeException exception) {
                errors.add(exception.getMessage() + " at " + node);
            }
        }

        return list;
    }

    public static void print(List<Node> list, PrintWriter out) throws IOException {
        int tab = 0;

        for (Node node : list) {
            if (node instanceof Operator) {
                Operator operator = (Operator) node;
                if (operator.priority == Operator.priorityOf("(")) {
                    if (operator.string == "{" | operator.string == "[" | operator.string == "(") {
                        node.print(out, tab++);
                    } else {
                        node.print(out, --tab);
                    }
                    continue;
                }
            }
            node.print(out, tab);
        }
    }

    // public static List<Node> foldTypes(List<Node> nodes) {
    // List<Node> list = new ArrayList<Node>();
    //
    // for (Node node : nodes) {
    // if (node instanceof KeyWord) {
    // KeyWord keyWord = (KeyWord) node;
    //
    // }
    // list.add(node);
    // }
    // return list;
    //
    // }

    public static List<Node> filterComments(List<Node> nodes) {
        List<Node> list = new ArrayList<Node>();

        for (Node node : nodes) {
            if (node instanceof Comment) {
                continue;
            }
            list.add(node);
        }
        return list;

    }

    // public static List<Node> foldKeyWords(List<Node> nodes) {
    // List<Node> list = new ArrayList<Node>();
    //
    // for (Node node : nodes) {
    // if (node instanceof SimpleString) {
    // SimpleString simpleString = (SimpleString) node;
    // if (KeyWord.isKeyWord(simpleString.string)) {
    // list.add(new KeyWord(simpleString));
    // continue;
    // }
    // }
    // list.add(node);
    // }
    //
    // return list;
    //
    // }
}
