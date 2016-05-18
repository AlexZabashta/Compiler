package misc;

import exception.TypeInitException;

public class Type {
    public final EnumType type;
    public final int dim;

    public final static int MAX_DIM = 3;

    public boolean idVoid() {
        return type == EnumType.VOID;
    }

    public static Type string() {
        try {
            return new Type(EnumType.CHAR, 1);
        } catch (TypeInitException neverHappen) {
            throw new RuntimeException(neverHappen);
        }
    }

    public Type() {
        this.type = EnumType.VOID;
        this.dim = 0;
    }

    public Type(EnumType type) {
        this.type = type;
        this.dim = 0;
    }

    public Type(EnumType type, int dim) throws TypeInitException {
        this.type = type;

        if (type == EnumType.VOID && dim != 0) {
            throw new TypeInitException("Void dim = " + dim + " != 0");
        }

        if (dim < 0) {
            throw new TypeInitException("Type dim = " + dim + " < 0");
        }

        if (dim > MAX_DIM) {
            throw new TypeInitException("Type dim = " + dim + " > " + MAX_DIM);
        }
        this.dim = dim;
    }

    public static Type get(String name, int dim) throws TypeInitException {
        switch (name.intern()) {
        case "int":
            return new Type(EnumType.INT, dim);
        case "char":
            return new Type(EnumType.CHAR, dim);
        case "bool":
            return new Type(EnumType.BOOL, dim);
        case "void":
            return new Type(EnumType.VOID, dim);
        case "string":
            return new Type(EnumType.CHAR, dim + 1);
        case "text":
            return new Type(EnumType.CHAR, dim + 2);
        default:
            throw new TypeInitException("Unknown type " + name);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dim;
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
        if (dim != other.dim)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (dim == 0) {
            return type.toString().toLowerCase();
        } else {
            return type.toString().toLowerCase() + "." + dim;
        }

    }
}
