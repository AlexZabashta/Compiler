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
import misc.Type;
import ast.node.op.FBracketsNode;
import exception.Log;
import exception.ParseException;
import exception.SyntaxesException;

public class Headers {

    public static List<Function> foldGlobal(List<Token> tokens, SimpleString pac, Log log) throws ParseException {

        List<Function> functions = new ArrayList<Function>();

        int size = tokens.size();

        for (int i = 0; i < size; i++) {
            Token token = tokens.get(i);

            if (token instanceof InitToken) {
                InitToken initToken = (InitToken) token;

                TypeToken initTypeToken = new TypeToken(new Type(), initToken.location);
                SimpleString initName = new SimpleString("init" + Characters.typeSeparator + "const", initToken.location);
                VarToken initVarToken = new VarToken(pac, initName);
                DeclarationToken initDeclaration = new DeclarationToken(initTypeToken, initVarToken);

                BracketsToken vars = (BracketsToken) tokens.get(i + 1);
                ++i;

                if (vars.type != BracketsType.ROUND) {
                    log.addException(new SyntaxesException("The variables shuld declared in round brackets", vars));
                }

                FBracketsNode actions = new FBracketsNode(initToken);
                try {
                    BracketsToken action = (BracketsToken) tokens.get(i + 1);
                    ++i;
                    actions = AST.parseFB(pac, action, log);
                } catch (IndexOutOfBoundsException | ClassCastException fake) {
                }

                List<DeclarationToken> gvars = new ArrayList<DeclarationToken>();

                for (DeclarationToken vard : varDecl(vars, log)) {
                    gvars.add(vard.addPac(pac, log));
                }

                InitFunction function = new InitFunction(initDeclaration, gvars, actions);
                functions.add(function);

                continue;
            }

            if (token instanceof DeclarationToken) {
                DeclarationToken declarationToken = (DeclarationToken) token;
                declarationToken = declarationToken.addPac(pac, log);

                BracketsToken vars = (BracketsToken) tokens.get(i + 1);
                ++i;

                if (vars.type != BracketsType.ROUND) {
                    log.addException(new SyntaxesException("The variables shuld declared in round brackets", vars));
                }

                BracketsToken action = (BracketsToken) tokens.get(i + 1);
                ++i;

                FBracketsNode actions = AST.parseFB(pac, action, log);
                List<DeclarationToken> lvars = new ArrayList<DeclarationToken>();

                for (DeclarationToken vard : varDecl(vars, log)) {
                    lvars.add(vard.removePac(log));
                }

                Function function = new Function(declarationToken, lvars, actions);
                functions.add(function);

                continue;
            }
            log.addException(new SyntaxesException("Unexpected token type" + token.getClass().getSimpleName(), token));

        }
        return functions;
    }

    public static List<DeclarationToken> varDecl(BracketsToken vars, Log log) throws ParseException {
        List<DeclarationToken> list = new ArrayList<DeclarationToken>();

        boolean sep = false;

        for (Token token : vars.tokens) {
            if (token instanceof Operator) {
                Operator operator = (Operator) token;
                if (operator.string != ",") {
                    log.addException(new SyntaxesException("Expected ',' as separator", token));
                }

                if (!sep) {
                    log.addException(new SyntaxesException("Expected var declaration", token));
                }
                sep = false;
                continue;
            }

            if (token instanceof DeclarationToken) {
                DeclarationToken declarationToken = (DeclarationToken) token;

                if (sep) {
                    log.addException(new SyntaxesException("Expected separator", token));
                }
                sep = true;

                list.add(declarationToken);
                continue;
            }

        }

        return list;
    }

}
