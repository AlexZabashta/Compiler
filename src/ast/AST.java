package ast;

import java.util.ArrayList;
import java.util.List;

import lex.Location;
import lex.Token;
import lex.token.fold.BracketsToken;
import lex.token.fold.BracketsType;
import lex.token.fold.BreakToken;
import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import lex.token.key_word.BoolToken;
import lex.token.key_word.ElseToken;
import lex.token.key_word.ForToken;
import lex.token.key_word.IfToken;
import lex.token.key_word.ReturnToken;
import lex.token.key_word.WhileToken;
import lex.token.pure.CharToken;
import lex.token.pure.NumberToken;
import lex.token.pure.Operator;
import lex.token.pure.QuotedString;
import ast.node.LValue;
import ast.node.RValue;
import ast.node.leaf.BoolNode;
import ast.node.leaf.CharNode;
import ast.node.leaf.DeclarationNode;
import ast.node.leaf.NumberNode;
import ast.node.leaf.QuotedStringNode;
import ast.node.leaf.VarNode;
import ast.node.misc.BreakNode;
import ast.node.misc.CallNode;
import ast.node.misc.ErrorNode;
import ast.node.misc.ForNode;
import ast.node.misc.IfNode;
import ast.node.misc.Nop;
import ast.node.misc.ReturnNode;
import ast.node.misc.ReturnVNode;
import ast.node.misc.WhileNode;
import ast.node.op.ArrayNode;
import ast.node.op.Assignment;
import ast.node.op.BOperatorNode;
import ast.node.op.FBracketsNode;
import ast.node.op.RBracketsNode;
import ast.node.op.Semicolon;
import ast.node.op.UOperatorNode;

public class AST {
    public static boolean isSep(Token token) {
        if (token instanceof Operator) {
            Operator operator = (Operator) token;
            return operator.string == ",";
        }
        return false;
    }

    public static Node parse(String pac, List<Token> tokens, int priority, List<String> errors) {
        if (tokens.isEmpty()) {
            return new Nop();
        }

        int size = tokens.size();

        if (priority < 11) {

            boolean inv = priority >= 2;

            for (int i = 0; i < size; i++) {
                int index = i;
                if (inv) {
                    index = size - i - 1;
                }

                Token token = tokens.get(index);

                if (token instanceof Operator) {
                    Operator operator = (Operator) token;
                    if (operator.priority == priority) {
                        if (operator.string == ",") {
                            errors.add("Use ';' as separator at " + token);
                            operator = new Operator(";", operator.location);
                        }
                        Node left, right;

                        if (inv) {
                            left = parse(pac, tokens.subList(0, index), priority, errors);
                            right = parse(pac, tokens.subList(index + 1, size), priority + 1, errors);
                        } else {
                            left = parse(pac, tokens.subList(0, index), priority + 1, errors);
                            right = parse(pac, tokens.subList(index + 1, size), priority, errors);
                        }

                        if (operator.string == ";") {
                            return new Semicolon(left, right, operator);
                        }

                        if (operator.string == "=") {
                            try {
                                return new Assignment((LValue) left, (RValue) right, operator);
                            } catch (ClassCastException fake) {
                                errors.add("Expected L-value before and R-value after " + operator);
                                ErrorNode node = new ErrorNode();
                                node.nodes.add(left);
                                node.nodes.add(right);
                                return node;
                            }
                        }

                        try {
                            return new BOperatorNode((RValue) left, (RValue) right, operator);
                        } catch (ClassCastException fake) {
                            errors.add("Expected R-value before and after " + operator);
                            ErrorNode node = new ErrorNode();
                            node.nodes.add(left);
                            node.nodes.add(right);
                            return node;
                        }
                    }

                }
            }

            return parse(pac, tokens, priority + 1, errors);
        }

        Token first = tokens.get(0);

        if (size == 1) {
            if (first instanceof BracketsToken) {
                BracketsToken bracketsToken = (BracketsToken) first;

                if (bracketsToken.type == BracketsType.FLOWER) {
                    return parseFB(pac, bracketsToken, errors);
                } else {
                    return parseRB(pac, bracketsToken, errors);
                }
            }

            try {
                return new BoolNode((BoolToken) first);
            } catch (ClassCastException fake) {
            }

            try {
                return new VarNode((VarToken) first);
            } catch (ClassCastException fake) {
            }

            try {
                return new DeclarationNode((DeclarationToken) first);
            } catch (ClassCastException fake) {
            }

            try {
                return new NumberNode((NumberToken) first);
            } catch (ClassCastException fake) {
            }

            try {
                return new QuotedStringNode((QuotedString) first);
            } catch (ClassCastException fake) {
            }

            try {
                return new CharNode((CharToken) first);
            } catch (ClassCastException fake) {
            }

            try {
                return new BreakNode((BreakToken) first);
            } catch (ClassCastException fake) {
            }

            try {
                return new ReturnNode((ReturnToken) first);
            } catch (ClassCastException fake) {
            }

            errors.add("Unexpected token " + first);

            return new Nop();
        }

        if (first instanceof ReturnToken) {
            ReturnToken returnToken = (ReturnToken) first;
            Node node = parse(pac, tokens.subList(1, size), priority, errors);
            try {
                return new ReturnVNode((RValue) node, returnToken);
            } catch (ClassCastException fake) {
                errors.add("Expected R-value  after " + returnToken);
                ErrorNode errorNode = new ErrorNode();
                errorNode.nodes.add(node);
                return errorNode;
            }
        }

        if (first instanceof Operator) {
            Operator operator = (Operator) first;
            if (operator.priority == 11) {
                Node node = parse(pac, tokens.subList(1, size), priority, errors);

                try {
                    return new UOperatorNode((RValue) node, operator);
                } catch (ClassCastException fake) {
                    errors.add("Expected R-value  after " + operator);
                    ErrorNode errorNode = new ErrorNode();
                    errorNode.nodes.add(node);
                    return errorNode;
                }
            }
        }

        Token last = tokens.get(size - 1);
        if (last instanceof BracketsToken) {
            BracketsToken bracketsToken = (BracketsToken) last;
            if (bracketsToken.type == BracketsType.SQUARE) {
                Node array = parse(pac, tokens.subList(0, size - 1), priority, errors);
                Node index = parse(pac, bracketsToken.tokens, 0, errors);

                try {
                    return new ArrayNode((RValue) array, (RValue) index, bracketsToken);
                } catch (ClassCastException fake) {
                    errors.add("Expected R-value at " + bracketsToken);
                    ErrorNode errorNode = new ErrorNode();
                    errorNode.nodes.add(array);
                    errorNode.nodes.add(index);
                    return errorNode;
                }

            }
        }

        if (size == 2) {
            try {
                VarToken varToken = (VarToken) first;

                if (varToken.pac == null) {
                    varToken = varToken.addPac(pac, errors);
                }
                BracketsToken bracketsToken = (BracketsToken) last;
                if (bracketsToken.type != BracketsType.ROUND) {
                    errors.add("Brackets must be round type " + bracketsToken);
                }
                List<Node> vars = split(pac, bracketsToken, errors);

                try {
                    List<RValue> args = new ArrayList<RValue>();
                    for (Node var : vars) {
                        args.add((RValue) var);
                    }
                    return new CallNode(varToken, args);
                } catch (ClassCastException fake) {
                    errors.add("Expected R-value as argument's of function " + varToken);
                    ErrorNode errorNode = new ErrorNode();
                    errorNode.nodes.addAll(vars);
                    return errorNode;
                }

            } catch (ClassCastException fake) {
            }
        }

        try {
            ForToken forToken = (ForToken) tokens.get(0);
            BracketsToken preStPost = (BracketsToken) tokens.get(1);
            if (preStPost.type != BracketsType.ROUND) {
                errors.add("Brackets must be round type " + preStPost);
            }
            Node action = parse(pac, tokens.subList(2, size), priority, errors);
            List<Node> vars = split(pac, preStPost, errors);
            if (vars.size() > 3) {
                errors.add("Too many nodes in () token " + preStPost);
                ErrorNode errorNode = new ErrorNode();
                errorNode.nodes.addAll(vars);
                errorNode.nodes.add(action);
                return errorNode;
            }

            try {
                if (vars.isEmpty()) {
                    return new ForNode(forToken, action);
                }

                if (vars.size() == 1) {
                    return new ForNode(forToken, (RValue) vars.get(0), action);
                }

                if (vars.size() == 2) {
                    return new ForNode(forToken, (RValue) vars.get(0), vars.get(1), action);
                }

                if (vars.size() == 3) {
                    return new ForNode(forToken, vars.get(0), (RValue) vars.get(1), vars.get(2), action);
                }
            } catch (ClassCastException f2) {
                errors.add("Expected empty or R-value as state " + preStPost);
                ErrorNode errorNode = new ErrorNode();
                errorNode.nodes.addAll(vars);
                errorNode.nodes.add(action);
                return errorNode;
            }

        } catch (ClassCastException | IndexOutOfBoundsException fake) {
        }

        try {
            WhileToken whileToken = (WhileToken) tokens.get(0);
            BracketsToken state = (BracketsToken) tokens.get(1);
            Node action = parse(pac, tokens.subList(2, size), priority, errors);
            if (state.tokens.isEmpty()) {
                RBracketsNode stateNode = new RBracketsNode(new BoolNode(new BoolToken(false, whileToken.location)), state);
                return new WhileNode(whileToken, stateNode, action);
            } else {
                RBracketsNode stateNode = parseRB(pac, state, errors);
                if (stateNode.node instanceof RValue) {
                    return new WhileNode(whileToken, stateNode, action);
                } else {
                    errors.add("Expected empty or R-value as state " + state);
                    ErrorNode errorNode = new ErrorNode();
                    errorNode.nodes.add(stateNode);
                    errorNode.nodes.add(action);
                    return errorNode;
                }
            }
        } catch (ClassCastException | IndexOutOfBoundsException fake) {
        }

        if (size == 5) {
            try {
                IfToken ifToken = (IfToken) tokens.get(0);
                BracketsToken state = (BracketsToken) tokens.get(1);

                BracketsToken x = (BracketsToken) tokens.get(2);
                ElseToken elseToken = (ElseToken) tokens.get(3);
                BracketsToken y = (BracketsToken) tokens.get(4);

                RBracketsNode stateNode = parseRB(pac, state, errors);

                if (x.type == BracketsType.ROUND) {

                    RBracketsNode xa = parseRB(pac, x, errors);
                    RBracketsNode ya = parseRB(pac, y, errors);
                    if (!(stateNode.node instanceof RValue)) {
                        errors.add("Expected empty or R-value as state " + state);
                        ErrorNode errorNode = new ErrorNode();
                        errorNode.nodes.add(stateNode);
                        errorNode.nodes.add(xa);
                        errorNode.nodes.add(ya);
                        return errorNode;
                    }

                    return new IfNode(ifToken, stateNode, xa, ya);
                } else {

                    FBracketsNode xa = parseFB(pac, x, errors);
                    FBracketsNode ya = parseFB(pac, y, errors);
                    if (!(stateNode.node instanceof RValue)) {
                        errors.add("Expected empty or R-value as state " + state);
                        ErrorNode errorNode = new ErrorNode();
                        errorNode.nodes.add(stateNode);
                        errorNode.nodes.add(xa);
                        errorNode.nodes.add(ya);
                        return errorNode;
                    }

                    return new IfNode(ifToken, stateNode, xa, ya);
                }

            } catch (ClassCastException fake) {
            }
        }
        if (size == 3) {
            try {
                IfToken ifToken = (IfToken) tokens.get(0);
                BracketsToken state = (BracketsToken) tokens.get(1);
                BracketsToken x = (BracketsToken) tokens.get(2);

                RBracketsNode stateNode = parseRB(pac, state, errors);

                if (x.type == BracketsType.ROUND) {
                    RBracketsNode xa = parseRB(pac, x, errors);
                    RBracketsNode ya = new RBracketsNode(new Nop(), new BracketsToken(BracketsType.ROUND, ifToken.location));
                    if (!(stateNode.node instanceof RValue)) {
                        errors.add("Expected empty or R-value as state " + state);
                        ErrorNode errorNode = new ErrorNode();
                        errorNode.nodes.add(stateNode);
                        errorNode.nodes.add(xa);
                        return errorNode;
                    }
                    return new IfNode(ifToken, stateNode, xa, ya);
                } else {
                    FBracketsNode xa = parseFB(pac, x, errors);
                    FBracketsNode ya = new FBracketsNode(new Nop(), new BracketsToken(BracketsType.FLOWER, ifToken.location));
                    if (!(stateNode.node instanceof RValue)) {
                        errors.add("Expected empty or R-value as state " + state);
                        ErrorNode errorNode = new ErrorNode();
                        errorNode.nodes.add(stateNode);
                        errorNode.nodes.add(xa);
                        return errorNode;
                    }
                    return new IfNode(ifToken, stateNode, xa, ya);
                }
            } catch (ClassCastException fake) {
            }
        }

        StringBuilder builder = new StringBuilder();

        Location location = null;

        for (Token token : tokens) {
            if (location == null) {
                location = token.location;
            }
            builder.append(' ');
            builder.append(token.toTokenString());
        }

        errors.add("Unexpected tokens combination " + builder + " at " + location);

        ErrorNode errorNode = new ErrorNode();

        for (int i = 0; i < size; i++) {
            errorNode.nodes.add(parse(pac, tokens.subList(i, i + 1), 0, errors));
        }

        return errorNode;
    }

    public static FBracketsNode parseFB(String pac, BracketsToken token, List<String> errors) {
        if (token.type != BracketsType.FLOWER) {
            errors.add("Expected {} brackets at " + token);
        }
        return new FBracketsNode(AST.parse(pac, token.tokens, 0, errors), token);
    }

    public static RBracketsNode parseRB(String pac, BracketsToken token, List<String> errors) {
        if (token.type != BracketsType.ROUND) {
            errors.add("Expected () brackets at " + token);
        }
        return new RBracketsNode(AST.parse(pac, token.tokens, 0, errors), token);
    }

    public static List<Node> split(String pac, BracketsToken bracketsToken, List<String> errors) {
        if (bracketsToken.type != BracketsType.ROUND) {
            errors.add("Expected () brackets at " + bracketsToken);
        }

        List<Node> vars = new ArrayList<Node>();

        List<Token> tokens = bracketsToken.tokens;
        int l = 0, n = tokens.size();

        while (l < n) {
            int r = l;
            while (r < n && !isSep(tokens.get(r))) {
                ++r;
            }
            vars.add(parse(pac, tokens.subList(l, r), 0, errors));
            l = r + 1;
        }

        return vars;
    }

}
