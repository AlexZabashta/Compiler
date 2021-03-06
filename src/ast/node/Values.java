package ast.node;

import java.util.List;

import misc.Characters;
import misc.Type;
import code.var.Variable;

public class Values {

    public static String toString(String prefix, List<Variable> vars) {
        StringBuilder builder = new StringBuilder();

        builder.append(prefix);
        for (Variable var : vars) {
            builder.append(Characters.typeSeparator);
            builder.append(var.type);
        }

        return builder.toString();
    }

    public static String toString(String prefix, Type... types) {
        StringBuilder builder = new StringBuilder();

        builder.append(prefix);
        for (Type type : types) {
            builder.append(Characters.typeSeparator);
            builder.append(type);
        }

        return builder.toString();
    }

}
