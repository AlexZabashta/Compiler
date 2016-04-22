package ast.node;

import java.util.List;

import lex.Token;
import lex.token.fold.DeclarationToken;
import misc.Characters;
import misc.Type;
import code.Variable;

public class Values {

    public static String toStringType(String prefix, List<Variable> vars) {
        StringBuilder builder = new StringBuilder();

        builder.append(prefix);
        for (Variable var : vars) {
            builder.append(Characters.typeSeparator);
            builder.append(var.type);
        }

        return builder.toString();
    }

    public static boolean cmp(Type dstType, Type srcType, List<String> errors, Token token) {
        if (dstType.equals(srcType)) {
            return true;
        }
        errors.add("Type mismatch " + srcType + " -> " + dstType + " at " + token);
        return false;
    }

    public static boolean cmp(Variable dst, Variable src, List<String> errors, Token token) {
        return cmp(dst.type, src.type, errors, token);
    }

    public static boolean cmp(Type dstType, DeclarationToken srcDec, List<String> errors) {
        return cmp(dstType, srcDec.typeToken.type, errors, srcDec);
    }

    public static boolean cmp(DeclarationToken dstDec, Type srcType, List<String> errors) {
        return cmp(dstDec.typeToken.type, srcType, errors, dstDec);
    }
}
