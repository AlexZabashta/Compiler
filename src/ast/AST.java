package ast;

import java.util.ArrayList;
import java.util.List;

import lex.Token;
import lex.token.fold.BracketsToken;
import lex.token.fold.BracketsType;
import lex.token.fold.DeclarationToken;
import lex.token.fold.TypeToken;
import lex.token.fold.VarToken;
import lex.token.key_word.InitToken;
import lex.token.pure.Operator;
import lex.token.pure.SimpleString;
import misc.Characters;
import misc.EnumType;
import misc.Type;

public class AST {

    public static List<Function> foldGlobal(List<Token> tokens, String pac, List<String> errors) {

        List<Function> functions = new ArrayList<Function>();

        int size = tokens.size();

        for (int i = 0; i < size; i++) {
            Token token = tokens.get(i);
            try {
                if (token instanceof InitToken) {
                    InitToken initToken = (InitToken) token;

                    TypeToken initTypeToken = new TypeToken(new Type(EnumType.VOID, 0), initToken.location);
                    SimpleString initPac = new SimpleString(pac, initToken.location);
                    SimpleString initName = new SimpleString("init" + Characters.typeSeparator + "const", initToken.location);
                    VarToken initVarToken = new VarToken(initPac, initName);
                    DeclarationToken initDeclaration = new DeclarationToken(initTypeToken, initVarToken);

                    BracketsToken vars = (BracketsToken) tokens.get(i + 1);
                    ++i;

                    if (vars.type != BracketsType.ROUND) {
                        errors.add("The variables shuld declared in round brackets");
                    }

                    FBracketsNode actions = new FBracketsNode();
                    try {
                        BracketsToken action = (BracketsToken) tokens.get(i + 1);
                        ++i;
                        if (action.type != BracketsType.FLOWER) {
                            errors.add("The action shuld  declared in flower brackets");
                        }
                        actions = parse(action, errors);
                    } catch (IndexOutOfBoundsException | ClassCastException fake) {
                    }

                    List<DeclarationToken> gvars = new ArrayList<DeclarationToken>();

                    for (DeclarationToken vard : varDecl(vars, errors)) {
                        gvars.add(vard.addPac(pac, errors));
                    }

                    InitFunction function = new InitFunction(initDeclaration, gvars, actions);
                    functions.add(function);

                    continue;
                }

                if (token instanceof DeclarationToken) {
                    DeclarationToken declarationToken = (DeclarationToken) token;
                    declarationToken = declarationToken.addPac(pac, errors);

                    BracketsToken vars = (BracketsToken) tokens.get(i + 1);
                    ++i;

                    if (vars.type != BracketsType.ROUND) {
                        errors.add("The variables shuld declared in round brackets");
                    }

                    BracketsToken action = (BracketsToken) tokens.get(i + 1);
                    ++i;
                    if (action.type != BracketsType.FLOWER) {
                        errors.add("The action shuld  declared in flower brackets");
                    }
                    FBracketsNode actions = parse(action, errors);
                    List<DeclarationToken> lvars = new ArrayList<DeclarationToken>();

                    for (DeclarationToken vard : varDecl(vars, errors)) {
                        lvars.add(vard.removePac(errors));
                    }

                    Function function = new Function(declarationToken, lvars, actions);
                    functions.add(function);

                    continue;
                }

                throw new RuntimeException("Unexpected token type " + token.getClass().getSimpleName());
            } catch (RuntimeException exception) {
                errors.add(exception.getMessage() + " at " + token);
            }
        }
        return functions;
    }

    public static FBracketsNode parse(BracketsToken action, List<String> errors) {
        FBracketsNode fBracketsNode = new FBracketsNode();
        return fBracketsNode;
    }

    public static List<DeclarationToken> varDecl(BracketsToken vars, List<String> errors) {
        List<DeclarationToken> list = new ArrayList<DeclarationToken>();

        boolean sep = false;

        for (Token token : vars.tokens) {
            if (token instanceof Operator) {
                Operator operator = (Operator) token;
                if (operator.string != ",") {
                    errors.add("Use '.' separator at " + operator);
                }

                if (!sep) {
                    errors.add("Expected var declaration at " + operator);
                }
                sep = false;
                continue;
            }

            if (token instanceof DeclarationToken) {
                DeclarationToken declarationToken = (DeclarationToken) token;

                if (sep) {
                    errors.add("Expected separator  at " + declarationToken);
                }
                sep = true;

                list.add(declarationToken);
                continue;
            }

        }

        return list;
    }

}
