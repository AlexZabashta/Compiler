package ast.node;

import java.util.List;

import lex.Token;
import lex.token.fold.DeclarationToken;
import misc.Characters;
import misc.Type;
import code.Variable;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

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

    public static boolean cmp(Type dstType, Type srcType, Log log, Token token) throws ParseException {
        if (dstType.equals(srcType)) {
            return true;
        }
        log.addException(new SemanticException("Type mismatch " + srcType + " -> " + dstType, token));
        return false;
    }

    public static boolean cmp(Variable dst, Variable src, Log log, Token token) throws ParseException {
        return cmp(dst.type, src.type, log, token);
    }

    public static boolean cmp(Type dstType, DeclarationToken srcDec, Log log) throws ParseException {
        return cmp(dstType, srcDec.typeToken.type, log, srcDec);
    }

    public static boolean cmp(DeclarationToken dstDec, Type srcType, Log log) throws ParseException {
        return cmp(dstDec.typeToken.type, srcType, log, dstDec);
    }
}
