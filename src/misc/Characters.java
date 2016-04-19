package misc;

public class Characters {

    public static boolean isChar(char symbol) {
        return ('a' <= symbol && symbol <= 'z') || ('A' <= symbol && symbol <= 'Z');
    }

    public static boolean isDigit(char symbol) {
        return '0' <= symbol && symbol <= '9';
    }

    public static boolean isEndOfLine(char symbol) {
        return symbol == 0 || symbol == '\n' || symbol == '\r';
    }

    public static boolean isBlank(char symbol) {
        return symbol == '\t' || symbol == ' ';
    }

    public static boolean isValid(char symbol) {
        if (symbol == 9 || symbol == 10 || symbol == 13) {
            return true;
        }
        return 32 <= symbol && symbol < 128;
    }

    public static final char typeSeparator = '$';

    public static char reEscape(char symbol) {
        switch (symbol) {
        case 't':
            return '\t';
        case 'b':
            return '\b';
        case 'n':
            return '\n';
        case 'r':
            return '\r';
        case 'f':
            return '\f';
        default:
            return symbol;
        }
    }

    public static String escape(char symbol) {
        switch (symbol) {
        case '\t':
            return "\\t";
        case '\b':
            return "\\b";
        case '\n':
            return "\\n";
        case '\r':
            return "\\r";
        case '\f':
            return "\\f";
        default:
            return Character.toString(symbol);
        }
    }

    public static String escape(String str) {
        str = str.replace("\t", "\\t");
        str = str.replace("\b", "\\b");
        str = str.replace("\n", "\\n");
        str = str.replace("\r", "\\r");
        str = str.replace("\f", "\\f");
        str = str.replace("\'", "\\\'");
        str = str.replace("\"", "\\\"");

        return str;
    }
}
