package ast;

import java.util.ArrayList;
import java.util.List;

import lex.Location;
import lex.Token;
import lex.token.ConstValueToken;
import lex.token.fold.BracketsToken;
import lex.token.fold.BracketsType;
import lex.token.fold.BreakToken;
import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import lex.token.key_word.ElseToken;
import lex.token.key_word.ForToken;
import lex.token.key_word.IfToken;
import lex.token.key_word.ReturnToken;
import lex.token.pure.Operator;
import lex.token.pure.SimpleString;
import ast.node.LValue;
import ast.node.RValue;
import ast.node.leaf.ConstValueNode;
import ast.node.leaf.DeclarationNode;
import ast.node.leaf.VarNode;
import ast.node.misc.BreakNode;
import ast.node.misc.CallNode;
import ast.node.misc.ErrorNode;
import ast.node.misc.ForNode;
import ast.node.misc.IfNode;
import ast.node.misc.Nop;
import ast.node.misc.ReturnNode;
import ast.node.misc.ReturnVNode;
import ast.node.op.ArrayNode;
import ast.node.op.Assignment;
import ast.node.op.BOperatorNode;
import ast.node.op.FBracketsNode;
import ast.node.op.RBracketsNode;
import ast.node.op.Semicolon;
import ast.node.op.UOperatorNode;
import exception.ASTException;
import exception.Log;
import exception.ParseException;
import exception.SyntaxesException;

public class AST {
    public static boolean isSep(Token token) {
        if (token instanceof Operator) {
            Operator operator = (Operator) token;
            return operator.string == ",";
        }
        return false;
    }

    public static Node parse(SimpleString pac, List<Token> tokens, int priority, Log log) throws ParseException {
        if (tokens.isEmpty()) {
            return new Nop();
        }

        int size = tokens.size();
        Token first = tokens.get(0);

        if ((priority == 1) && (first instanceof ReturnToken)) {
            ReturnToken returnToken = (ReturnToken) first;
            Node node = parse(pac, tokens.subList(1, size), priority, log);
            try {
                return new ReturnVNode((RValue) node, returnToken);
            } catch (ClassCastException fake) {
                log.addException(new ASTException("Expected R-value after", returnToken));
                ErrorNode errorNode = new ErrorNode();
                errorNode.nodes.add(node);
                return errorNode;
            }
        }

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
                            log.addException(new SyntaxesException("Expected ';' as separator", token));
                            operator = new Operator(";", operator.location);
                        }
                        Node left, right;

                        if (inv) {
                            left = parse(pac, tokens.subList(0, index), priority, log);
                            right = parse(pac, tokens.subList(index + 1, size), priority + 1, log);
                        } else {
                            left = parse(pac, tokens.subList(0, index), priority + 1, log);
                            right = parse(pac, tokens.subList(index + 1, size), priority, log);
                        }

                        if (operator.string == ";") {
                            return new Semicolon(left, right, operator);
                        }

                        if (operator.string == "=") {
                            try {
                                return new Assignment((LValue) left, (RValue) right, operator);
                            } catch (ClassCastException fake) {
                                log.addException(new ASTException("Expected L-value before and R-value after", token));
                                ErrorNode node = new ErrorNode();
                                node.nodes.add(left);
                                node.nodes.add(right);
                                return node;
                            }
                        }

                        try {
                            return new BOperatorNode((RValue) left, (RValue) right, operator);
                        } catch (ClassCastException fake) {
                            log.addException(new ASTException("Expected R-value before and after", operator));
                            ErrorNode node = new ErrorNode();
                            node.nodes.add(left);
                            node.nodes.add(right);
                            return node;
                        }
                    }

                }
            }

            return parse(pac, tokens, priority + 1, log);
        }

        if (size == 1) {
            if (first instanceof BracketsToken) {
                BracketsToken bracketsToken = (BracketsToken) first;

                if (bracketsToken.type == BracketsType.FLOWER) {
                    return parseFB(pac, bracketsToken, log);
                } else {
                    return parseRB(pac, bracketsToken, log);
                }
            }

            try {
                return new ConstValueNode((ConstValueToken) first);
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
                return new BreakNode((BreakToken) first);
            } catch (ClassCastException fake) {
            }

            try {
                return new ReturnNode((ReturnToken) first);
            } catch (ClassCastException fake) {
            }

            log.addException(new SyntaxesException("Unexpected token", first));

            return new Nop();
        }

        if (first instanceof Operator) {
            Operator operator = (Operator) first;
            if (operator.priority == 11) {
                Node node = parse(pac, tokens.subList(1, size), priority, log);

                try {
                    return new UOperatorNode((RValue) node, operator);
                } catch (ClassCastException fake) {
                    log.addException(new ASTException("Expected R-value after", operator));
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
                Node array = parse(pac, tokens.subList(0, size - 1), priority, log);
                Node index = parse(pac, bracketsToken.tokens, 0, log);

                try {
                    return new ArrayNode((RValue) array, (RValue) index, bracketsToken);
                } catch (ClassCastException fake) {
                    log.addException(new ASTException("Expected R-value in brackets", bracketsToken));
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
                    varToken = varToken.addPac(pac, log);
                }
                BracketsToken bracketsToken = (BracketsToken) last;
                if (bracketsToken.type != BracketsType.ROUND) {
                    log.addException(new ASTException("Expected round brackets", bracketsToken));
                }
                List<Node> vars = split(pac, bracketsToken, log);

                try {
                    List<RValue> args = new ArrayList<RValue>();
                    for (Node var : vars) {
                        args.add((RValue) var);
                    }
                    return new CallNode(varToken, args);
                } catch (ClassCastException fake) {
                    log.addException(new ASTException("Expected R-value as argument's of function", bracketsToken));
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
                log.addException(new ASTException("Expected round brackets", preStPost));
            }
            Node action = parse(pac, tokens.subList(2, size), priority, log);
            List<Node> vars = split(pac, preStPost, log);
            if (vars.size() > 3) {
                log.addException(new ASTException("Too many nodes in brackets", preStPost));
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
                log.addException(new ASTException("Expected empty or R-value as state", preStPost));
                ErrorNode errorNode = new ErrorNode();
                errorNode.nodes.addAll(vars);
                errorNode.nodes.add(action);
                return errorNode;
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

                RBracketsNode stateNode = parseRB(pac, state, log);

                if (x.type == BracketsType.ROUND) {

                    RBracketsNode xa = parseRB(pac, x, log);
                    RBracketsNode ya = parseRB(pac, y, log);
                    if (!(stateNode.node instanceof RValue)) {
                        log.addException(new ASTException("Expected empty or R-value as state", state));
                        ErrorNode errorNode = new ErrorNode();
                        errorNode.nodes.add(stateNode);
                        errorNode.nodes.add(xa);
                        errorNode.nodes.add(ya);
                        return errorNode;
                    }

                    return new IfNode(ifToken, stateNode, xa, ya);
                } else {

                    FBracketsNode xa = parseFB(pac, x, log);
                    FBracketsNode ya = parseFB(pac, y, log);
                    if (!(stateNode.node instanceof RValue)) {
                        log.addException(new ASTException("Expected empty or R-value as state", state));
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

                RBracketsNode stateNode = parseRB(pac, state, log);

                if (x.type == BracketsType.ROUND) {
                    RBracketsNode xa = parseRB(pac, x, log);
                    RBracketsNode ya = new RBracketsNode(new Nop(), new BracketsToken(BracketsType.ROUND, ifToken.location));
                    if (!(stateNode.node instanceof RValue)) {
                        log.addException(new ASTException("Expected empty or R-value as state", state));
                        ErrorNode errorNode = new ErrorNode();
                        errorNode.nodes.add(stateNode);
                        errorNode.nodes.add(xa);
                        return errorNode;
                    }
                    return new IfNode(ifToken, stateNode, xa, ya);
                } else {
                    FBracketsNode xa = parseFB(pac, x, log);
                    FBracketsNode ya = new FBracketsNode(new Nop(), new BracketsToken(BracketsType.FLOWER, ifToken.location));
                    if (!(stateNode.node instanceof RValue)) {
                        log.addException(new ASTException("Expected empty or R-value as state", state));
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

        log.addException(new SyntaxesException("Unexpected tokens combination", builder.toString(), location));

        ErrorNode errorNode = new ErrorNode();

        for (int i = 0; i < size; i++) {
            errorNode.nodes.add(parse(pac, tokens.subList(i, i + 1), 0, log));
        }

        return errorNode;
    }

    public static FBracketsNode parseFB(SimpleString pac, BracketsToken token, Log log) throws ParseException {
        if (token.type != BracketsType.FLOWER) {
            log.addException(new SyntaxesException("Expected {} brackets", token));
        }
        return new FBracketsNode(AST.parse(pac, token.tokens, 0, log), token);
    }

    public static RBracketsNode parseRB(SimpleString pac, BracketsToken token, Log log) throws ParseException {
        if (token.type != BracketsType.ROUND) {
            log.addException(new SyntaxesException("Expected () brackets", token));
        }
        return new RBracketsNode(AST.parse(pac, token.tokens, 0, log), token);
    }

    public static List<Node> split(SimpleString pac, BracketsToken bracketsToken, Log log) throws ParseException {
        if (bracketsToken.type != BracketsType.ROUND) {
            log.addException(new SyntaxesException("Expected () brackets", bracketsToken));
        }

        List<Node> vars = new ArrayList<Node>();

        List<Token> tokens = bracketsToken.tokens;
        int l = 0, n = tokens.size();

        while (l < n) {
            int r = l;
            while (r < n && !isSep(tokens.get(r))) {
                ++r;
            }
            vars.add(parse(pac, tokens.subList(l, r), 0, log));
            l = r + 1;
        }

        return vars;
    }

}
