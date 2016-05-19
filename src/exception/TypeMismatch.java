package exception;

import misc.Type;

public class TypeMismatch extends Exception {

    private static final long serialVersionUID = 1L;

    public TypeMismatch(Type dst, Type src) {
        super("Type mismatch " + src + " -> " + dst);
    }

    public static void check(Type dst, Type src) throws TypeMismatch {
        if (!dst.equals(src)) {
            throw new TypeMismatch(dst, src);
        }
    }
}
