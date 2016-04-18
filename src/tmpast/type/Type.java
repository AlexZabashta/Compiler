package tmpast.type;

import java.util.List;

import lex.Location;

public class Type {
    final EnumType type;
    final int level;

    public Type(EnumType type, int level) {
        this.type = type;

        if (type == EnumType.VOID && level != 0) {
            throw new RuntimeException("Void level = " + level + " != 0");
        }

        if (level < 0) {
            throw new RuntimeException("Type level = " + level + " < 0");
        }

        if (level > 15) {
            throw new RuntimeException("Type level = " + level + " > 15");
        }
        this.level = level;
    }

    public static Type get(String name, int level) {
        switch (name.intern()) {
        case "int":
            return new Type(EnumType.INT, level);
        case "char":
            return new Type(EnumType.CHAR, level);
        case "bool":
            return new Type(EnumType.BOOL, level);
        case "void":
            return new Type(EnumType.VOID, level);
        case "string":
            return new Type(EnumType.CHAR, level + 1);
        case "text":
            return new Type(EnumType.CHAR, level + 2);
        default:
            throw new RuntimeException("Unknown type " + name);
        }
    }

    @Override
    public String toString() {
        return type + "_" + level;
    }
}
