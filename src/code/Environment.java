package code;

import java.util.Map;

import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import ast.Function;
import lex.Token;
import lex.token.fold.DeclarationToken;

public class Environment {
    public final Map<String, Variable> lv;
    public final Map<String, DeclarationToken> gv;
    public final Map<String, Function> f;

    public Environment(Map<String, Variable> lv, Map<String, DeclarationToken> gv, Map<String, Function> f) {
        this.lv = lv;
        this.gv = gv;
        this.f = f;
    }

    public Function getFunction(String funStr, Log log, Token token) throws ParseException {
        Function function = f.get(funStr);

        if (function == null) {
            log.addException(new SemanticException("Can't find " + funStr, token));
        }

        return function;
    }

}
