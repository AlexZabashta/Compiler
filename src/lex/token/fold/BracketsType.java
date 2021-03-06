package lex.token.fold;

public enum BracketsType {
    FLOWER, ROUND, SQUARE;

    public static String close(BracketsType type) {
        switch (type) {
        case SQUARE:
            return "]";
        case ROUND:
            return ")";
        case FLOWER:
            return "}";
        default:
            return "";
        }
    }

    public static BracketsType get(String bracket) {
        switch (bracket) {
        case "[":
        case "]":
            return SQUARE;
        case "{":
        case "}":
            return FLOWER;
        case "(":
        case ")":
            return ROUND;
        default:
            return null;
        }
    }

    public static boolean isClose(String bracket) {
        switch (bracket) {
        case "]":
        case "}":
        case ")":
            return true;
        default:
            return false;
        }
    }

    public static boolean isOpen(String bracket) {
        switch (bracket) {
        case "[":
        case "{":
        case "(":
            return true;
        default:
            return false;
        }
    }

    public static String open(BracketsType type) {
        switch (type) {
        case SQUARE:
            return "[";
        case ROUND:
            return "(";
        case FLOWER:
            return "{";
        default:
            return "";
        }
    }
}
