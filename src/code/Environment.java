package code;

import java.util.Map;

import lex.token.fold.DeclarationToken;
import ast.Function;

public class Environment {
    public final Map<String, Variable> lv;
    public final Map<String, DeclarationToken> gv;
    public final Map<String, Function> f;

    public Environment(Map<String, Variable> lv, Map<String, DeclarationToken> gv, Map<String, Function> f) {
        this.lv = lv;
        this.gv = gv;
        this.f = f;
    }

}
