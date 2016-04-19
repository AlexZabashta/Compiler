package misc;

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + level;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Type other = (Type) obj;
        if (level != other.level)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (level == 0) {
            return type.toString().toLowerCase();
        } else {
            return type.toString().toLowerCase() + "." + level;
        }

    }
}
